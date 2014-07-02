// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.4.3.
// Generated from `gamenews.jce'
// **********************************************************************

package com.duowan.gamenews;

public final class Comment extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "gamenews.Comment";
    }

    public String fullClassName()
    {
        return "com.duowan.gamenews.Comment";
    }

    public String id = "";

    public String content = "";

    public com.duowan.gamenews.User user = null;

    public int time = 0;

    public com.duowan.gamenews.Count count = null;

    public String getId()
    {
        return id;
    }

    public void  setId(String id)
    {
        this.id = id;
    }

    public String getContent()
    {
        return content;
    }

    public void  setContent(String content)
    {
        this.content = content;
    }

    public com.duowan.gamenews.User getUser()
    {
        return user;
    }

    public void  setUser(com.duowan.gamenews.User user)
    {
        this.user = user;
    }

    public int getTime()
    {
        return time;
    }

    public void  setTime(int time)
    {
        this.time = time;
    }

    public com.duowan.gamenews.Count getCount()
    {
        return count;
    }

    public void  setCount(com.duowan.gamenews.Count count)
    {
        this.count = count;
    }

    public Comment()
    {
        setId(id);
        setContent(content);
        setUser(user);
        setTime(time);
        setCount(count);
    }

    public Comment(String id, String content, com.duowan.gamenews.User user, int time, com.duowan.gamenews.Count count)
    {
        setId(id);
        setContent(content);
        setUser(user);
        setTime(time);
        setCount(count);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        Comment t = (Comment) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(id, t.id) && 
            com.duowan.taf.jce.JceUtil.equals(content, t.content) && 
            com.duowan.taf.jce.JceUtil.equals(user, t.user) && 
            com.duowan.taf.jce.JceUtil.equals(time, t.time) && 
            com.duowan.taf.jce.JceUtil.equals(count, t.count) );
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
        if (null != id)
        {
            _os.write(id, 0);
        }
        if (null != content)
        {
            _os.write(content, 1);
        }
        if (null != user)
        {
            _os.write(user, 2);
        }
        _os.write(time, 3);
        if (null != count)
        {
            _os.write(count, 4);
        }
    }

    static com.duowan.gamenews.User cache_user;
    static com.duowan.gamenews.Count cache_count;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setId( _is.readString(0, false));

        setContent( _is.readString(1, false));

        if(null == cache_user)
        {
            cache_user = new com.duowan.gamenews.User();
        }
        setUser((com.duowan.gamenews.User) _is.read(cache_user, 2, false));

        setTime((int) _is.read(time, 3, false));

        if(null == cache_count)
        {
            cache_count = new com.duowan.gamenews.Count();
        }
        setCount((com.duowan.gamenews.Count) _is.read(cache_count, 4, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(id, "id");
        _ds.display(content, "content");
        _ds.display(user, "user");
        _ds.display(time, "time");
        _ds.display(count, "count");
    }

}

