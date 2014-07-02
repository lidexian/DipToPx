// **********************************************************************
// This file was generated by a TAF parser!
// TAF version 2.1.4.3.
// Generated from `CommBase.jce'
// **********************************************************************

package com.duowan.Comm;

public final class AppUA extends com.duowan.taf.jce.JceStruct implements java.lang.Cloneable
{
    public String className()
    {
        return "Comm.AppUA";
    }

    public String fullClassName()
    {
        return "com.duowan.Comm.AppUA";
    }

    public int ePlat = 0;

    public String sVersion = "";

    public String sChannel = "";

    public int iWidth = 0;

    public int iHeight = 0;

    public String sOSVersion = "";

    public String sDevice = "";

    public int getEPlat()
    {
        return ePlat;
    }

    public void  setEPlat(int ePlat)
    {
        this.ePlat = ePlat;
    }

    public String getSVersion()
    {
        return sVersion;
    }

    public void  setSVersion(String sVersion)
    {
        this.sVersion = sVersion;
    }

    public String getSChannel()
    {
        return sChannel;
    }

    public void  setSChannel(String sChannel)
    {
        this.sChannel = sChannel;
    }

    public int getIWidth()
    {
        return iWidth;
    }

    public void  setIWidth(int iWidth)
    {
        this.iWidth = iWidth;
    }

    public int getIHeight()
    {
        return iHeight;
    }

    public void  setIHeight(int iHeight)
    {
        this.iHeight = iHeight;
    }

    public String getSOSVersion()
    {
        return sOSVersion;
    }

    public void  setSOSVersion(String sOSVersion)
    {
        this.sOSVersion = sOSVersion;
    }

    public String getSDevice()
    {
        return sDevice;
    }

    public void  setSDevice(String sDevice)
    {
        this.sDevice = sDevice;
    }

    public AppUA()
    {
        setEPlat(ePlat);
        setSVersion(sVersion);
        setSChannel(sChannel);
        setIWidth(iWidth);
        setIHeight(iHeight);
        setSOSVersion(sOSVersion);
        setSDevice(sDevice);
    }

    public AppUA(int ePlat, String sVersion, String sChannel, int iWidth, int iHeight, String sOSVersion, String sDevice)
    {
        setEPlat(ePlat);
        setSVersion(sVersion);
        setSChannel(sChannel);
        setIWidth(iWidth);
        setIHeight(iHeight);
        setSOSVersion(sOSVersion);
        setSDevice(sDevice);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        AppUA t = (AppUA) o;
        return (
            com.duowan.taf.jce.JceUtil.equals(ePlat, t.ePlat) && 
            com.duowan.taf.jce.JceUtil.equals(sVersion, t.sVersion) && 
            com.duowan.taf.jce.JceUtil.equals(sChannel, t.sChannel) && 
            com.duowan.taf.jce.JceUtil.equals(iWidth, t.iWidth) && 
            com.duowan.taf.jce.JceUtil.equals(iHeight, t.iHeight) && 
            com.duowan.taf.jce.JceUtil.equals(sOSVersion, t.sOSVersion) && 
            com.duowan.taf.jce.JceUtil.equals(sDevice, t.sDevice) );
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
        _os.write(ePlat, 0);
        if (null != sVersion)
        {
            _os.write(sVersion, 1);
        }
        if (null != sChannel)
        {
            _os.write(sChannel, 2);
        }
        _os.write(iWidth, 3);
        _os.write(iHeight, 4);
        if (null != sOSVersion)
        {
            _os.write(sOSVersion, 5);
        }
        if (null != sDevice)
        {
            _os.write(sDevice, 6);
        }
    }

    static int cache_ePlat;

    public void readFrom(com.duowan.taf.jce.JceInputStream _is)
    {
        setEPlat((int) _is.read(ePlat, 0, false));

        setSVersion( _is.readString(1, false));

        setSChannel( _is.readString(2, false));

        setIWidth((int) _is.read(iWidth, 3, false));

        setIHeight((int) _is.read(iHeight, 4, false));

        setSOSVersion( _is.readString(5, false));

        setSDevice( _is.readString(6, false));

    }

    public void display(java.lang.StringBuilder _os, int _level)
    {
        com.duowan.taf.jce.JceDisplayer _ds = new com.duowan.taf.jce.JceDisplayer(_os, _level);
        _ds.display(ePlat, "ePlat");
        _ds.display(sVersion, "sVersion");
        _ds.display(sChannel, "sChannel");
        _ds.display(iWidth, "iWidth");
        _ds.display(iHeight, "iHeight");
        _ds.display(sOSVersion, "sOSVersion");
        _ds.display(sDevice, "sDevice");
    }

}
