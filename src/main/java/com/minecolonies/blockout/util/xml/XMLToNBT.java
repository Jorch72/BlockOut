package com.minecolonies.blockout.util.xml;

import com.minecolonies.blockout.util.NBTType;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class XMLToNBT
{
    private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN          = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern FLOAT_PATTERN           = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern BYTE_PATTERN            = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern LONG_PATTERN            = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern SHORT_PATTERN           = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern INT_PATTERN             = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");

    private static final Map<Pattern, NBTType>                 TYPE_MATCHING_PATTERNS    = new LinkedHashMap<>();
    private static final Map<NBTType, Function<Node, NBTBase>> TYPE_CONVERSION_FUNCTIONS = new HashMap<>();
    static
    {
        TYPE_MATCHING_PATTERNS.put(DOUBLE_PATTERN_NOSUFFIX, NBTType.TAG_DOUBLE);
        TYPE_MATCHING_PATTERNS.put(DOUBLE_PATTERN, NBTType.TAG_DOUBLE);
        TYPE_MATCHING_PATTERNS.put(FLOAT_PATTERN, NBTType.TAG_FLOAT);
        TYPE_MATCHING_PATTERNS.put(BYTE_PATTERN, NBTType.TAG_BYTE);
        TYPE_MATCHING_PATTERNS.put(LONG_PATTERN, NBTType.TAG_LONG);
        TYPE_MATCHING_PATTERNS.put(SHORT_PATTERN, NBTType.TAG_SHORT);
        TYPE_MATCHING_PATTERNS.put(INT_PATTERN, NBTType.TAG_INT);
    }

    static
    {
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_BYTE, (node) -> convertFromValue(node, (byteString) -> new NBTTagByte(Byte.parseByte(byteString.replace("b", "")))));
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_BYTE_ARRAY, XMLToNBT::convertToByteArray);
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_COMPOUND, XMLToNBT::convertToNBTTagCompound);
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_DOUBLE, (node) -> convertFromValue(node, (doubleString) -> new NBTTagDouble(Double.parseDouble(doubleString.replace("d", "")))));
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_FLOAT, (node) -> convertFromValue(node, (floatString) -> new NBTTagFloat(Float.parseFloat(floatString.replace("f", "")))));
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_SHORT, (node) -> convertFromValue(node, (shortString) -> new NBTTagShort(Short.parseShort(shortString.replace("s", "")))));
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_LONG, (node) -> convertFromValue(node, (longString) -> new NBTTagLong(Long.parseLong(longString.replace("l", "")))));
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_INT, (node) -> convertFromValue(node, (intString) -> new NBTTagInt(Integer.parseInt(intString))));
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_LIST, XMLToNBT::convertToList);
        TYPE_CONVERSION_FUNCTIONS.put(NBTType.TAG_STRING, (node) -> convertFromValue(node, NBTTagString::new));
    }
    private XMLToNBT()
    {
        throw new IllegalArgumentException("Utility class: XMLToNBT");
    }

    /**
     * Maps a given XML Node onto a NBTBase.
     *
     * @param node The node to map.
     * @return The mapped NBTBase.
     *
     * @throws IllegalArgumentException when the XML syntax does not match.
     */
    public static final NBTBase fromXML(@NotNull final Node node) throws IllegalArgumentException
    {
        return TYPE_CONVERSION_FUNCTIONS.get(getNBTType(node)).apply(node);
    }

    private static NBTBase convertToByteArray(@NotNull final Node node)
    {
        return new NBTTagByteArray(
          XMLStreamSupport.streamChildren(node)
            .filter(child -> child.getNodeType() != 8)
            .filter(child -> child.getNodeType() != 3)
            .map(TYPE_CONVERSION_FUNCTIONS.get(NBTType.TAG_BYTE)::apply)
            .map(nbtBase -> (NBTTagByte) nbtBase)
            .map(NBTTagByte::getByte)
            .collect(Collectors.toList())
        );
    }

    private static <T extends NBTBase> NBTBase convertFromValue(@NotNull final Node node, @NotNull final Function<String, T> converter)
    {
        if (node.getAttributes().getLength() != 1)
        {
            throw new IllegalArgumentException(String.format("Too many or not enough attributes on Node: %s only value is valid.", node.toString()));
        }

        final Node valueNode = node.getAttributes().getNamedItem("value");

        return converter.apply(valueNode.getNodeValue());
    }

    private static NBTBase convertToList(@NotNull final Node node)
    {
        final NBTTagList list = new NBTTagList();

        XMLStreamSupport.streamChildren(node)
          .filter(child -> child.getNodeType() != 8)
          .filter(child -> child.getNodeType() != 3)
          .map(child -> TYPE_CONVERSION_FUNCTIONS.get(getNBTType(child)).apply(child))
          .forEach(list::appendTag);

        return list;
    }

    private static final NBTType getNBTType(@NotNull final Node node) throws IllegalArgumentException
    {
        if (!node.hasAttributes())
        {
            if (!node.hasChildNodes())
            {
                throw new IllegalArgumentException(String.format("Can not determine NBT Type from node: %s", node.toString()));
            }

            final long childrenTypeCount = XMLStreamSupport.streamChildren(node)
                                             .filter(child -> child.getNodeType() != 3)
                                             .filter(child -> child.getNodeType() != 8)
                                             .map(XMLToNBT::getNBTType)
                                             .distinct()
                                             .count();

            final long childrenNameCount = XMLStreamSupport.streamChildren(node)
                                             .filter(child -> child.getNodeType() != 3)
                                             .filter(child -> child.getNodeType() != 8)
                                             .map(Node::getNodeName)
                                             .distinct()
                                             .count();

            if (childrenNameCount == 1)
            {
                if (childrenTypeCount == 1)
                {
                    final NBTType type = XMLStreamSupport.streamChildren(node)
                                           .filter(child -> child.getNodeType() != 3)
                                           .filter(child -> child.getNodeType() != 8)
                                           .map(XMLToNBT::getNBTType).distinct().findFirst().get();
                    if (type == NBTType.TAG_BYTE)
                    {
                        return NBTType.TAG_BYTE_ARRAY;
                    }

                    return NBTType.TAG_LIST;
                }
            }

            return NBTType.TAG_COMPOUND;
        }

        if (node.hasChildNodes())
        {
            throw new IllegalArgumentException(String.format("Can not determine NBT Type from node: %s. A node with children is not allowed to have attributes.", node.toString()));
        }

        if (node.getAttributes().getLength() > 1)
        {
            throw new IllegalArgumentException(String.format("Too many attributes on Node: %s only none or value are valid.", node.toString()));
        }

        final Node valueNode = node.getAttributes().getNamedItem("value");

        if (valueNode == null)
        {
            throw new IllegalArgumentException(String.format("No value attribute specified on node: %s", node.toString()));
        }

        return TYPE_MATCHING_PATTERNS
                 .entrySet()
                 .stream()
                 .filter(
                   tmpe -> tmpe
                             .getKey()
                             .matcher(valueNode.getNodeValue())
                             .matches()
                 )
                 .map(Map.Entry::getValue)
                 .findFirst()
                 .orElse(NBTType.TAG_STRING);
    }

    private static final NBTBase convertToNBTTagCompound(@NotNull final Node node)
    {
        final NBTTagCompound compound = new NBTTagCompound();

        XMLStreamSupport.streamChildren(node)
          .filter(child -> child.getNodeType() != 8)
          .filter(child -> child.getNodeType() != 3)
          .forEach(child -> {
              final NBTBase nbtBase = TYPE_CONVERSION_FUNCTIONS.get(getNBTType(child)).apply(child);
              final String name = child.getNodeName();

              if (compound.hasKey(name))
              {
                  throw new IllegalArgumentException(String.format("Given node contains multiple entries with the same key: %s", name));
              }

              compound.setTag(name, nbtBase);
          });

        return compound;
    }
}