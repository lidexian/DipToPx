// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.4.3.
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class Count extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.Count";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.Count";
    }

    public int likeCount = 0;

    public int dislikeCount = 0;

    public int getLikeCount()
    {
        return likeCount;
    }

    public void  setLikeCount(int likeCount)
    {
        this.likeCount = likeCount;
    }

    public int getDislikeCount()
    {
        return dislikeCount;
    }

    public void  setDislikeCount(int dislikeCount)
    {
        this.dislikeCount = dislikeCount;
    }

    public Count()
    {
        setLikeCount(likeCount);
        setDislikeCount(dislikeCount);
    }

    public Count(int likeCount, int dislikeCount)
    {
        setLikeCount(likeCount);
        setDislikeCount(dislikeCount);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        Count t = (Count) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(likeCount, t.likeCount) && 
            com.duowan.taf.jce.JceUtil.equals(dislikeCount, t.dislikeCount) );
    }

    public int hashCode()
    {
        try
        {
            throw new Exception("Need define key first!");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }
    public java.lang.Object clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void writeTo(com.duowan.taf.jce.JceOutputStream _os)
    {
        _os.write(likeCount, 0);
        _os.write(dislikeCount, 1);
    }


    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setLikeCount((int) _is.read(likeCount, 0, false));

        setDislikeCount((int) _is.read(dislikeCount, 1, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(likeCount, "likeCount");
        _ds.display(dislikeCount, "dislikeCount");
    }

}

