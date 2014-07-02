// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.4.3.
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class FavType
{
    private static FavType[] __values = new FavType[2];
    private int __value;
    private String __T = new String();

    public static final int _FAV_TYPE_ADD = 1;
    public static final FavType FAV_TYPE_ADD = new FavType(0,_FAV_TYPE_ADD,"FAV_TYPE_ADD");
    public static final int _FAV_TYPE_DEL = 2;
    public static final FavType FAV_TYPE_DEL = new FavType(1,_FAV_TYPE_DEL,"FAV_TYPE_DEL");

    public static FavType convert(int val)
    {
        for(int __i = 0; __i < __values.length; ++__i)
        {
            if(__values[__i].value() == val)
            {
                return __values[__i];
            }
        }
        assert false;
        return null;
    }

    public static FavType convert(String val)
    {
        for(int __i = 0; __i < __values.length; ++__i)
        {
            if(__values[__i].toString().equals(val))
            {
                return __values[__i];
            }
        }
        assert false;
        return null;
    }

    public int value()
    {
        return __value;
    }

    public String toString()
    {
        return __T;
    }

    private FavType(int index, int val, String s)
    {
        __T = s;
        __value = val;
        __values[index] = this;
    }

}
