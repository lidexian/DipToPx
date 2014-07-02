// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.4.3.
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class ArticleType
{
    private static ArticleType[] __values = new ArticleType[5];
    private int __value;
    private String __T = new String();

    public static final int _ARTICLE_TYPE_ARTICLE = 1;
    public static final ArticleType ARTICLE_TYPE_ARTICLE = new ArticleType(0,_ARTICLE_TYPE_ARTICLE,"ARTICLE_TYPE_ARTICLE");
    public static final int _ARTICLE_TYPE_SPECIAL = 2;
    public static final ArticleType ARTICLE_TYPE_SPECIAL = new ArticleType(1,_ARTICLE_TYPE_SPECIAL,"ARTICLE_TYPE_SPECIAL");
    public static final int _ARTICLE_TYPE_CAIDAN = 3;
    public static final ArticleType ARTICLE_TYPE_CAIDAN = new ArticleType(2,_ARTICLE_TYPE_CAIDAN,"ARTICLE_TYPE_CAIDAN");
    public static final int _ARTICLE_TYPE_ACTIVITY = 4;
    public static final ArticleType ARTICLE_TYPE_ACTIVITY = new ArticleType(3,_ARTICLE_TYPE_ACTIVITY,"ARTICLE_TYPE_ACTIVITY");
    public static final int _ARTICLE_TYPE_BANG = 5;
    public static final ArticleType ARTICLE_TYPE_BANG = new ArticleType(4,_ARTICLE_TYPE_BANG,"ARTICLE_TYPE_BANG");

    public static ArticleType convert(int val)
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

    public static ArticleType convert(String val)
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

    private ArticleType(int index, int val, String s)
    {
        __T = s;
        __value = val;
        __values[index] = this;
    }

}