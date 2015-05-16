
package org.atennert.homectrl;


public enum DataType
{
    INTEGER, DOUBLE, STRING, BOOL, DATE;

    public static Object getTypeValue(DataType type, String value)
    {
        switch ( type )
        {
            case DOUBLE :
                return Double.valueOf(getValidNumberString(value));

            case INTEGER :
                return Integer.valueOf(getValidNumberString(value));

            case STRING :
                return value == null ? "" : value;

            case BOOL :
                return new Boolean(Boolean.parseBoolean(value) || ( Integer.parseInt(getValidNumberString(value)) > 1 ));

            default :
                return null;
        }
    }

    private static String getValidNumberString(String value)
    {
        return value == null || "".equals(value) ? "0" : value;
    }

    public static DataType getDataTypeFromString(String type){
        String bigType = type.toUpperCase();

        if ("BOOLEAN".equals(bigType) || "BOOL".equals(bigType))
        {
            return DataType.BOOL;
        }
        else if ("INTEGER".equals(bigType) || "INT".equals(bigType))
        {
            return DataType.INTEGER;
        }
        else if ("DOUBLE".equals(bigType))
        {
            return DataType.DOUBLE;
        }
        else if ("STRING".equals(bigType))
        {
            return DataType.STRING;
        }
        else if ("DATE".equals(bigType))
        {
            return DataType.DATE;
        }
        else
        {
            throw new RuntimeException("Unable to parse type: " + type);
        }
    }
}
