package com.fgtit.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.SystemClock;

import java.util.HashMap;
import java.util.Iterator;

@SuppressLint("NewApi")
public class UsbModule {

	public static final String ACTION_USB_PERMISSION = "com.example.fpdemo.USB";

	private final static int TIME_OUT=10000;

	private final static int EP_IN = 0x81;
    private final static int EP_OUT = 0x02;

    private final static int Usb_Request_Type0 = 0x80;
    private final static int Usb_Request_Type1 = 0x00;
    private final static int Usb_Request_Type2 = 0x00;
    private final static int Usb_Request_Type3 = 0x20;
    private final static int Usb_Request_Type4 = 0x40;
    private final static int Usb_Request_Type5 = 0x60;
    private final static int Usb_Request_Type6 = 0x00;
    private final static int Usb_Request_Type7 = 0x01;
    private final static int Usb_Request_Type8 = 0x02;
    private final static int Usb_Request_Type9 = 0x03;


    private final static int   MAX_PACKAGE_SIZE  =  350;
    private final static int   HEAD_LENGTH       =  3;
    private final static byte  CMD      =  0x01;
    private final static byte  DATA     =  0x02;
    private final static byte  ENDDATA  =  0x08;
    private final static byte  RESPONSE =  0x07;

    private final static int IMAGE_X = 256;
    private final static int IMAGE_Y = 288;

    private final static byte    	GET_IMAGE			=0x01;
    private final static byte    	GEN_CHAR			=0x02;
    private final static byte    	MATCH				=0x03;
    private final static byte    	SEARCH			    =0x04;
    private final static byte    	REG_MODULE			=0x05;
    private final static byte    	STORE_CHAR			=0x06;
    private final static byte    	LOAD_CHAR			=0x07;
    private final static byte    	UP_CHAR				=0x08;
    private final static byte    	DOWN_CHAR			=0x09;
    private final static byte    	UP_IMAGE		    =0x0a;
    private final static byte    	DOWN_IMAGE			=0x0b;
    private final static byte    	DEL_CHAR		    =0x0c;
    private final static byte    	EMPTY 		        =0x0d;
    private final static byte		WRITE_REG			=0x0e;
    private final static byte     	READ_PAR_TABLE      =0x0f;
    private final static byte     	ENROLL              =0x10;
    private final static byte     	IDENTIFY            =0x11;
    private final static byte    	SET_PWD				=0x12;
    private final static byte    	VFY_PWD				=0x13;
    private final static byte     	GET_RANDOM          =0x14;
    private final static byte     	SET_CHIPADDR        =0x15;
    private final static byte     	READ_INFPAGE        =0x16;
    private final static byte    	PORT_CONTROL		=0x17;
    private final static byte    	WRITE_NOTEPAD   	=0x18;
    private final static byte    	READ_NOTEPAD 		=0x19;
    private final static byte     	BURN_CODE           =0x1a;
    private final static byte     	HIGH_SPEED_SEARCH   =0x1b;
    private final static byte     	GEN_BINIMAGE        =0x1c;
    private final static byte     	TEMPLATE_NUM        =0x1d;
    private final static byte     	USERDEFINE          =0x1e;
    private final static byte     	READ_INDEXTABLE     =0x1f;
    private final static byte		GETIMAGEX			=0x30;
    private final static byte		UPIMAGEX			=0x31;
    private final static byte		GENCHAREX			=0x32;
    private final static byte		CALIBRATESENSOR		=0x40;
    private final static byte		READCARDSN		=0x50;
    private final static byte		OPENDOOR		=0x54;
    private final static byte     	REG_BAUD            =4;
    private final static byte    	REG_SECURE_LEVEL	=5;
    private final static byte		REG_PACKETSIZE		=6;

    private final static byte    	GET_IMAGEEX			=0x30;
    private final static byte    	UP_IMAGEEX		    =0x31;
    private final static byte    	GEN_CHAREX			=0x32;

    public final static int FPM_DEVICE		=0x01;
    public final static int FPM_PLACE		=0x02;
    public final static int FPM_LIFT		=0x03;
    public final static int FPM_CAPTURE		=0x04;
    public final static int FPM_GENCHAR		=0x05;
    public final static int FPM_ENRFPT		=0x06;
    public final static int FPM_NEWIMAGE	=0x07;
    public final static int FPM_TIMEOUT		=0x08;
    public final static int FPM_IMGVAL		=0x09;

    public final static int RET_OK			=1;
    public final static int RET_FAIL		=0;

    private final static int NCM_IMAGE	=0x01;
    private final static int NCM_ENROL	=0x02;
    private final static int NCM_GENCHAR=0x03;

    private Handler mHandler=null;
    private int	g_iworkmsg[]=new int[5];
    private int	g_iretmsg[]=new int[5];
    private int	g_imsginc=0;

    private boolean 	g_IsOpen=false;
    private boolean	g_IsLink=false;
    private boolean  g_bExit=false;

    private int g_iTimeCount=0;
    private boolean g_bIsStart=false;

    public byte bmpdata[]=new byte[74806];
    public int bmpsize[]=new int[1];
    public byte rawdata[]=new byte[73728];
    public int rawsize[]=new int[1];

    public byte refdata[]=new byte[512];
    public int refsize[]=new int[1];
    public byte matdata[]=new byte[512];
    public int matsize[]=new int[1];

    private Context pContext=null;
    private int			devtype=0;
	private UsbManager usbManager=null;
	private UsbDevice usbDevice=null;
	private UsbDeviceConnection usbConn=null;
	private UsbEndpoint usbEndpointIn = null;
	private UsbEndpoint usbEndpointOut = null;
	private int 	m_nEPInSize;
	private int 	m_nEPOutSize;
    public boolean bAutoPermission=false;
    //private PendingIntent mPermissionIntent;

    private boolean bUpImage=true;

    public void SetUpImage(boolean isup){
    	bUpImage=isup;
    }

    /////////////////////////////////////////////////////////////////////////////////
    private void DebugShowInfo(byte[] data,int size){
    	String msg="";
    	for(int i=0;i<size;i++) {
    		msg=msg+","+ Integer.toHexString(data[i]&0xFF).toUpperCase();
    	}
    	/*Toast.makeText(pContext, msg,5000).show();*/
    }

    @SuppressLint("NewApi")
	private boolean usb_get_device()
	{
    	if(pContext==null)
    		return false;

    	usbManager=(UsbManager)pContext.getSystemService(Context.USB_SERVICE);
		if(usbManager==null)
		{
			return false;
		}

		HashMap<String,UsbDevice> devlist=usbManager.getDeviceList();
		Iterator<UsbDevice> deviter= devlist.values().iterator();
		while(deviter.hasNext())
		{
			UsbDevice tmpusbdev = deviter.next();
			if((tmpusbdev.getVendorId()==0x0453)&&(tmpusbdev.getProductId()==0x9005))
			{
				devtype=0;
				usbDevice=tmpusbdev;
				break;
			}else if((tmpusbdev.getVendorId()==0x2009)&&(tmpusbdev.getProductId()==0x7638))
			{
				devtype=1;
				usbDevice=tmpusbdev;
				break;
			}else if((tmpusbdev.getVendorId()==0x2109)&&(tmpusbdev.getProductId()==0x7638))
			{
				devtype=1;
				usbDevice=tmpusbdev;
				break;
			}else if((tmpusbdev.getVendorId()==0x0483)&&(tmpusbdev.getProductId()==0x5720))
			{
				devtype=2;
				usbDevice=tmpusbdev;
				break;
			}
		}
		if(usbDevice==null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@SuppressLint("NewApi")
	private boolean usb_open_device()
	{
		usbConn = usbManager.openDevice(usbDevice);
		if(usbConn==null)
		{
			return false;
		}
		else
		{
			UsbInterface usbInterface = usbDevice.getInterface(0);//usbDevice.getInterface(1);
			usbConn.claimInterface(usbInterface, true);

			for (int i = 0; i < usbInterface.getEndpointCount(); i++)
			{
				if (usbInterface.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
				{
					if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
					{
						usbEndpointIn = usbInterface.getEndpoint(i);
					}
					else if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_OUT)
					{
						usbEndpointOut = usbInterface.getEndpoint(i);
					}
				}
			}
			m_nEPInSize = usbEndpointIn.getMaxPacketSize();
	        m_nEPOutSize = usbEndpointOut.getMaxPacketSize();

			return true;
		}
	}

	@SuppressLint("NewApi")
	private boolean usb_close_device()
	{
		if(usbConn!=null)
		{
			usbConn.close();
		}
		return true;
	}

	@SuppressLint("NewApi")
	private int usb_controlmsg(int requesttype, int request,int value, int index, byte[] buffer, int length,int timeout)
	{
		if(usbConn!=null)
			return usbConn.controlTransfer(requesttype, request, value, index, buffer, length, timeout);
		else
			return -1;
	}

	@SuppressLint("NewApi")
	private int usb_bulkread(byte[] buffer, int length, int timeout){
		if(usbConn!=null)
			return usbConn.bulkTransfer(usbEndpointIn, buffer, length, timeout);
		else
			return -1;
	}

	@SuppressLint("NewApi")
	private int usb_read_device(byte[] databuf,int datasize,int timeout){
		if(usbConn!=null){
			/*
			int r=1;
			r=usbConn.bulkTransfer(usbEndpointIn,databuf,datasize,timeout);
			if(r>=0)
				return 0;
			else
				return -1;
			*/
			int n = datasize / m_nEPInSize;
	        int r = datasize % m_nEPInSize;
	        int rs=0;
	        int i=0;
	        byte[] tmp = new byte[512];
	        for(i=0; i<n; i++){
	            rs = usbConn.bulkTransfer(usbEndpointIn, tmp, m_nEPInSize, timeout);
	            //if (rs != m_nEPInSize)
	            //    return -1;
	            System.arraycopy(tmp, 0, databuf, i*m_nEPInSize, m_nEPInSize);
	        }
	        if (r > 0){
	            rs = usbConn.bulkTransfer(usbEndpointIn, tmp, r, timeout);
	            //if (rs != r)
	            //    return -1;
	            System.arraycopy(tmp, 0, databuf, i*m_nEPInSize, r);
	        }
	        return 0;
		}else{
			return -1;
		}
	}

	@SuppressLint("NewApi")
	private int usb_write_device(byte[] databuf,int datasize,int timeout){
		if(usbConn!=null){
			/*
			int r=1;
			r=usbConn.bulkTransfer(usbEndpointOut,databuf,datasize,timeout);
			if(r>=0)
				return 0;
			else
				return -2;
			*/
			int n = datasize / m_nEPOutSize;
	        int r = datasize % m_nEPOutSize;
	        int rs=0;
	        int i=0;
	        byte[] tmp = new byte[512];
	        for(i=0; i<n; i++){
	            System.arraycopy(databuf, i*m_nEPOutSize, tmp, 0, m_nEPOutSize);
	            rs = usbConn.bulkTransfer(usbEndpointOut, tmp, m_nEPOutSize, timeout);
	            //if (rs != m_nEPOutSize)
	            //    return -2;
	        }
	        if (r > 0){
	            System.arraycopy(databuf, i*m_nEPOutSize, tmp, 0, r);
	            rs = usbConn.bulkTransfer(usbEndpointOut, tmp, r, timeout);
	            //if (rs != r)
	            //    return -2;
	        }
	        return 0;
		}else{
			return -2;
		}
	}

/////////////////////////////////////////////////////////////////////////////////
	private boolean LibUSBOpen()
	{
		if(usb_get_device())
		{
			if(usb_open_device())
			{
				return true;
			}
		}
		return false;
	}

	private boolean LibUSBClose()
	{
		return usb_close_device();
	}

	private int LibUSBDownData(byte[] DataBuf,int nLen)
	{
		SystemClock.sleep(50);
		switch(devtype){
		case 0:{
				int nRet=0;
				byte buf[]=new byte[10];
				int r=usb_controlmsg((Usb_Request_Type0 | Usb_Request_Type4 | Usb_Request_Type1),0,nLen,0,buf,10,TIME_OUT);
				nRet=usb_write_device (DataBuf,nLen,TIME_OUT);
				return nRet;
			}
		case 1:{
				int nRet=0;
				byte do_CBW[]=new byte[33];
				byte di_CSW[]=new byte[16];
				for(int i=0;i<33;i++)
					do_CBW[i]=(byte)0x00;
				do_CBW[0]=(byte)0x55;	do_CBW[1]=(byte)0x53;	do_CBW[2]=(byte)0x42;	do_CBW[3]=(byte)0x43;
				do_CBW[4]=(byte)0xB0;	do_CBW[5]=(byte)0xFA;	do_CBW[6]=(byte)0x69;	do_CBW[7]=(byte)0x86;
				do_CBW[8]=(byte)0x00;	do_CBW[9]=(byte)0x00;	do_CBW[10]=(byte)0x00;	do_CBW[11]=(byte)0x00;
				do_CBW[12]=(byte)0x00;	do_CBW[13]=(byte)0x00;	do_CBW[14]=(byte)0x0A;	do_CBW[15]=(byte)0x86;
				for(int i=0;i<16;i++)
					di_CSW[i]=(byte)0x00;
				do_CBW[8] = (byte) (nLen & 0xff);
				do_CBW[9] = (byte) ((nLen>>8) & 0xff);
				do_CBW[10]= (byte) ((nLen>>16)& 0xff);
				do_CBW[11]= (byte) ((nLen>>24)& 0xff);
				int r=usb_write_device(do_CBW,31,TIME_OUT);
				if(r!=0)
					return -1;
				int ret=usb_write_device(DataBuf,nLen,TIME_OUT);
				if(ret!=0)
					return -1;
				ret = usb_read_device(di_CSW,13,TIME_OUT);
				if((byte)di_CSW[3]!=(byte)0x53 || (byte)di_CSW[12]!=(byte)0x00)
					return -1;
				di_CSW[3]=0x43;
				for(int i=0; i<12; i++){
					if((byte)di_CSW[i]!=(byte)do_CBW[i])
						return -1;
				}
				return nRet;
			}
		case 2:{
				int nRet=0;
				byte do_CBW[]=new byte[33];
				byte di_CSW[]=new byte[16];
				for(int i=0;i<33;i++)
					do_CBW[i]=(byte)0x00;
				do_CBW[0]=(byte)0x55;	do_CBW[1]=(byte)0x53;	do_CBW[2]=(byte)0x42;	do_CBW[3]=(byte)0x43;
				do_CBW[4]=(byte)0xB0;	do_CBW[5]=(byte)0xFA;	do_CBW[6]=(byte)0x69;	do_CBW[7]=(byte)0x86;
				do_CBW[8]=(byte)0x00;	do_CBW[9]=(byte)0x00;	do_CBW[10]=(byte)0x00;	do_CBW[11]=(byte)0x00;
				do_CBW[12]=(byte)0x00;	do_CBW[13]=(byte)0x00;	do_CBW[14]=(byte)0x0A;	do_CBW[15]=(byte)0x86;
				for(int i=0;i<16;i++)
					di_CSW[i]=(byte)0x00;
				do_CBW[8] = (byte) (nLen & 0xff);
				do_CBW[9] = (byte) ((nLen>>8) & 0xff);
				do_CBW[10]= (byte) ((nLen>>16)& 0xff);
				do_CBW[11]= (byte) ((nLen>>24)& 0xff);
				int r=usb_write_device(do_CBW,31,TIME_OUT);
				if(r!=0)
					return -1;
				int ret=usb_write_device(DataBuf,nLen,TIME_OUT);
				if(ret!=0)
					return -1;
				ret = usb_read_device(di_CSW,13,TIME_OUT);
				if((byte)di_CSW[3]!=(byte)0x53 || (byte)di_CSW[12]!=(byte)0x00)
					return -1;
				di_CSW[3]=0x43;
				//DebugShowInfo(do_CBW,13);
				//DebugShowInfo(di_CSW,13);
				/*
				for(int i=0; i<12; i++){
					if((byte)di_CSW[i]!=(byte)do_CBW[i])
						return -1;
				}
				*/
				return nRet;
			}
		}
		return -1;
	}

	private int LibUSBDownData1(byte[] DataBuf,int nLen)
	{
		switch(devtype){
		case 0:{
				int nRet=0;
				byte buf[]=new byte[10];
				int r=usb_controlmsg((Usb_Request_Type0 | Usb_Request_Type4 | Usb_Request_Type1),1,nLen,0,buf,10,TIME_OUT);
				nRet=usb_write_device (DataBuf,nLen,TIME_OUT);
				return nRet;
			}
		case 1:{
			return LibUSBDownData(DataBuf,nLen);
			}
		case 2:{
			return LibUSBDownData(DataBuf,nLen);
			}
		}
		return -1;
	}

	private int LibUSBGetData(byte[] DataBuf,int nLen,int Timeout)
	{
		switch(devtype){
		case 0:{
				int nRet=0;
				byte buf[]=new byte[10];
				int r=usb_controlmsg((Usb_Request_Type0 | Usb_Request_Type4 | Usb_Request_Type1),1,nLen,0,buf,10,TIME_OUT);
				//nRet=usb_read_device (DataBuf,nLen,TIME_OUT);
				r=usb_bulkread(DataBuf,nLen,TIME_OUT);
				if(r>=0)
					nRet=0;
				else
					nRet=-1;
				return nRet;
			}
		case 1:{
			int nRet=0;
			byte do_CBW[]=new byte[33];
			byte di_CSW[]=new byte[16];
			for(int i=0;i<33;i++)
				do_CBW[i]=(byte)0x00;
			do_CBW[0]=(byte)0x55;	do_CBW[1]=(byte)0x53;	do_CBW[2]=(byte)0x42;	do_CBW[3]=(byte)0x43;
			do_CBW[4]=(byte)0xB0;	do_CBW[5]=(byte)0xFA;	do_CBW[6]=(byte)0x69;	do_CBW[7]=(byte)0x86;
			do_CBW[8]=(byte)0x00;	do_CBW[9]=(byte)0x00;	do_CBW[10]=(byte)0x00;	do_CBW[11]=(byte)0x00;
			do_CBW[12]=(byte)0x80;	do_CBW[13]=(byte)0x00;	do_CBW[14]=(byte)0x0A;	do_CBW[15]=(byte)0x85;
			for(int i=0;i<16;i++)
				di_CSW[i]=(byte)0x00;
			do_CBW[8] = (byte) (nLen & 0xff);
			do_CBW[9] = (byte) ((nLen>>8) & 0xff);
			do_CBW[10]= (byte) ((nLen>>16)& 0xff);
			do_CBW[11]= (byte) ((nLen>>24)& 0xff);

			int r=usb_write_device(do_CBW,31,TIME_OUT);
			if(r<0)
				return -1;
			int ret=usb_read_device(DataBuf,nLen,TIME_OUT);
			if(ret<0)
				return -1;
			ret = usb_read_device(di_CSW,13,TIME_OUT);
			if((byte)di_CSW[3]!=(byte)0x53 || (byte)di_CSW[12]!=(byte)0x00)
				return -1;
			for(int i=4; i<8; i++){
				if((byte)di_CSW[i]!=(byte)do_CBW[i])
					return -1;
			}
			return nRet;
			}
		case 2:{
			int nRet=0;
			byte do_CBW[]=new byte[33];
			byte di_CSW[]=new byte[16];
			for(int i=0;i<33;i++)
				do_CBW[i]=(byte)0x00;
			do_CBW[0]=(byte)0x55;	do_CBW[1]=(byte)0x53;	do_CBW[2]=(byte)0x42;	do_CBW[3]=(byte)0x43;
			do_CBW[4]=(byte)0xB0;	do_CBW[5]=(byte)0xFA;	do_CBW[6]=(byte)0x69;	do_CBW[7]=(byte)0x86;
			do_CBW[8]=(byte)0x00;	do_CBW[9]=(byte)0x00;	do_CBW[10]=(byte)0x00;	do_CBW[11]=(byte)0x00;
			do_CBW[12]=(byte)0x80;	do_CBW[13]=(byte)0x00;	do_CBW[14]=(byte)0x0A;	do_CBW[15]=(byte)0x85;
			for(int i=0;i<16;i++)
				di_CSW[i]=(byte)0x00;
			do_CBW[8] = (byte) (nLen & 0xff);
			do_CBW[9] = (byte) ((nLen>>8) & 0xff);
			do_CBW[10]= (byte) ((nLen>>16)& 0xff);
			do_CBW[11]= (byte) ((nLen>>24)& 0xff);

			int r=usb_write_device(do_CBW,31,Timeout);
			if(r<0)
				return -1;
			int ret=usb_read_device(DataBuf,nLen,Timeout);
			if(ret<0)
				return -1;
			ret = usb_read_device(di_CSW,13,Timeout);
			if((byte)di_CSW[3]!=(byte)0x53 || (byte)di_CSW[12]!=(byte)0x00)
				return -1;
			/*
			for(int i=4; i<8; i++){
				if((byte)di_CSW[i]!=(byte)do_CBW[i])
					return -1;
			}
			*/
			return nRet;
			}
		}
		return -1;

	}

	private int LibUSBGetImage(byte[] DataBuf,int nLen)
	{
		switch(devtype){
		case 0:{
			int r=1;
			byte buf[]=new byte[10];
			int n=8;
			int len=nLen/n;
			byte tmp[]=new byte[len];

			r=usb_controlmsg((Usb_Request_Type0 | Usb_Request_Type4 | Usb_Request_Type1),1,nLen,0,buf,10,TIME_OUT);
			for(int k=0;k<8;k++)
			{
				r=usb_bulkread(tmp,len,TIME_OUT);
				int t=len*k;
				for(int i=0;i<len;i++)
				{
					DataBuf[t+i]=tmp[i];
				}
			}
			if(r>=0)
			{
				return 0;
			}
			else
				return -1;
			}
		case 1:{
			int r=1;
			int n=8;
			int len=nLen/n;
			byte tmp[]=new byte[len];
			for(int k=0;k<n;k++){
				r=LibUSBGetData(tmp,len,TIME_OUT);
				int t=len*k;
				for(int i=0;i<len;i++){
					DataBuf[t+i]=tmp[i];
				}
			}
			if(r>=0)
				return 0;
			else
				return -1;
			/*
			int r=1;
			int n=8;
			int len=nLen/n;
			byte tmp[]=new byte[len];
			for(int k=0;k<8;k++)
			{
				r=LibUSBGetData(tmp,len,TIME_OUT);
				int t=len*k;
				for(int i=0;i<len;i++)
				{
					DataBuf[t+i]=tmp[i];
				}
			}
			if(r>=0)
			{
				return 0;
			}
			else
				return -1;
			*/
			/*
			int iTmpLen=nLen;
			iTmpLen=nLen/2;
			if(LibUSBGetData(DataBuf,iTmpLen,TIME_OUT)!=0)
				return -1;
			return LibUSBGetData(DataBuf+iTmpLen,iTmpLen,TIME_OUT);
			*/
			}
		case 2:{
			///*
			int r=1;
			int n=2;
			int len=nLen/n;
			byte tmp[]=new byte[len];
			for(int k=0;k<n;k++){
				r=LibUSBGetData(tmp,len,TIME_OUT*5);
				int t=len*k;
				for(int i=0;i<len;i++){
					DataBuf[t+i]=tmp[i];
				}
			}
			if(r>=0)
				return 0;
			else
				return -1;
			//*/
			/*
			byte do_CBW[]=new byte[33];
			byte di_CSW[]=new byte[16];
			for(int i=0;i<33;i++)
				do_CBW[i]=(byte)0x00;
			do_CBW[0]=(byte)0x55;	do_CBW[1]=(byte)0x53;	do_CBW[2]=(byte)0x42;	do_CBW[3]=(byte)0x43;
			do_CBW[4]=(byte)0xB0;	do_CBW[5]=(byte)0xFA;	do_CBW[6]=(byte)0x69;	do_CBW[7]=(byte)0x86;
			do_CBW[8]=(byte)0x00;	do_CBW[9]=(byte)0x00;	do_CBW[10]=(byte)0x00;	do_CBW[11]=(byte)0x00;
			do_CBW[12]=(byte)0x80;	do_CBW[13]=(byte)0x00;	do_CBW[14]=(byte)0x0A;	do_CBW[15]=(byte)0x85;
			for(int i=0;i<16;i++)
				di_CSW[i]=(byte)0x00;
			do_CBW[8] = (byte) (nLen & 0xff);
			do_CBW[9] = (byte) ((nLen>>8) & 0xff);
			do_CBW[10]= (byte) ((nLen>>16)& 0xff);
			do_CBW[11]= (byte) ((nLen>>24)& 0xff);

			int r=usb_write_device(do_CBW,31,TIME_OUT);
			if(r<0)
				return -1;

			int n=2;
			int len=nLen/n;
			byte tmp[]=new byte[len];
			for(int k=0;k<n;k++){
				r=usb_read_device(tmp,len,TIME_OUT);
				usb_read_device(di_CSW,13,TIME_OUT);
				int t=len*k;
				for(int i=0;i<len;i++){
					DataBuf[t+i]=tmp[i];
				}
			}
			return 0;
			*/
			/*
			int ret = usb_read_device(di_CSW,13,TIME_OUT);
			if((byte)di_CSW[3]!=(byte)0x53 || (byte)di_CSW[12]!=(byte)0x00)
				return -1;
			return ret;
			*/
			}
		}
		return -1;
	}

	private int LibUSBDownImage(byte[] DataBuf,int nLen)
	{
		switch(devtype){
		case 0:{
			int nRet=0;
			byte buf[]=new byte[10];
			int len=nLen/4;
			byte tmp[]=new byte[len];

			int r=usb_controlmsg((Usb_Request_Type0 | Usb_Request_Type4 | Usb_Request_Type1),1,nLen,0,buf,10,TIME_OUT);
			for(int i=0;i<len;i++)
			{
				tmp[i]=DataBuf[i];
			}
			nRet=usb_write_device(tmp,len,TIME_OUT);	//8000
			if(nRet!=0) return nRet;
			for(int i=0;i<len;i++)
			{
				tmp[i]=DataBuf[i+len];
			}
			nRet=usb_write_device(tmp,len,TIME_OUT); //8000
			if(nRet!=0) return nRet;
			for(int i=0;i<len;i++)
			{
				tmp[i]=DataBuf[i+len*2];
			}
			nRet=usb_write_device(tmp,len,TIME_OUT);  //8000
			if(nRet!=0) return nRet;
			for(int i=0;i<len;i++)
			{
				tmp[i]=DataBuf[i+len*3];
			}
			nRet=usb_write_device(tmp,len,TIME_OUT);   //8000
			return nRet;
			}
		case 1:{
			return LibUSBDownData(DataBuf,nLen);
			}
		case 2:{
			return LibUSBDownData(DataBuf,nLen);
			}
		}
		return -1;
	}

/////////////////////////////////////////////////////////////////////////////////
	private boolean EnCode(int nAddr, byte[] pSource, int iSourceLength, byte[] pDestination, int[] iDestinationLength)
	{
		int i, n;

		iDestinationLength[0] = 0;

		if ( iSourceLength > MAX_PACKAGE_SIZE-4 )  //?????????????????????????????????
			return false;

		pDestination[0]=(byte)0xEF;
		pDestination[1]=(byte)0x01;
		pDestination[2]=(byte)((nAddr>>24)&0xff);
		pDestination[3]=(byte)((nAddr>>16)&0xff);
		pDestination[4]=(byte)((nAddr>>8)&0xff);
		pDestination[5]=(byte)(nAddr&0xff);

		i = 0;
		n=6;
		int ChkSum=0;

		for (i=0; i<iSourceLength-2; i++)
		{
			ChkSum+=(pSource[i]);
			pDestination[n++]=(pSource[i]);
		}
	    int ValH,ValL;
	    ValL=ChkSum&0xff;
		ValH=(ChkSum>>8)&0xff;
		pDestination[n++]=(byte) ValH;
		pDestination[n++]=(byte) ValL;

		iDestinationLength[0] = iSourceLength+6;

		return true;
	}

	private boolean	DeCode(byte[] pSource, int iSourceLength, byte[] pDestination, int[] iDestinationLength)
	{
		iDestinationLength[0] = 0;
		int tag1=(pSource[0] >=0 ? pSource[0] : pSource[0] + 256);
		int tag2=(pSource[1] >=0 ? pSource[1] : pSource[1] + 256);
		//if(pSource[0]!=0xEF || pSource[1]!=0x01) //byte??????????????????????????????????
		if(tag1!=0xEF || tag2!=0x01)
		{
			return false;
		}

		int hi=(pSource[7] >=0 ? pSource[7] : pSource[7] + 256);
		int lo=(pSource[8] >=0 ? pSource[8] : pSource[8] + 256);
		//int nLen=((pSource[7])<<8)+(pSource[8])+1;
		int nLen=((hi<<8)&0xff00)+(lo)+1;

		for(int i=0;i<nLen;i++)
			pDestination[i]=(pSource[i+6]);

		iDestinationLength[0] = nLen;

		return true;
	}

	private boolean	GetPackage(byte[] pData,int nLen,int nTimeOut)
	{
		byte recvBuf[]=new byte[1024];
		int nDecodedLen[]=new int[1];
		if (nTimeOut!=0)
		{
			if(LibUSBGetData(recvBuf,nLen,nTimeOut)!=0)
				return false;
		}
		else
		{
			if(LibUSBGetData(recvBuf,nLen,TIME_OUT)!=0)
				return false;
		}

		//????????????
		if ( !DeCode(recvBuf, nLen, pData, nDecodedLen) )
			return false;

		return true;
	}

	private int GetPackageLength(byte[] pData)
	{
	    // |  ??????????????   |   ??????????????????	  |   ...{????????????}     |  ???????????    |
	    // |  1 byte   |     2 bytes  |	  ...{??????????????????}   |  2 bytes   |
		int length = 0;
		length = pData[1]*256 + pData[2] + 1 +2;
		return length;
	}

	private int GetPackageContentLength(byte[] pData)
	{
		int length = 0;
		length = pData[1]*256 + pData[2];
		return length;
	}

	private boolean	SendPackage(int nAddr,byte[] pData)
	{
		int iLength;
		int iEncodedLength[]=new int[1];
		byte encodedBuf[]=new byte[MAX_PACKAGE_SIZE+20];
		boolean bSuccess = false;

		iLength = GetPackageLength(pData);  //??????????????????????????
		if (iLength>MAX_PACKAGE_SIZE)
			return false;

		//????????????
		if ( !EnCode(nAddr,pData, iLength, encodedBuf, iEncodedLength) )
			return false;

		if (iEncodedLength[0] > MAX_PACKAGE_SIZE)  //????????????????????????????????????
			return false;

		if(LibUSBDownData(encodedBuf,iEncodedLength[0])!=0)
			return false;

		return true;
	}

	private int FillPackage(byte[] pData, int nPackageType, int nLength, byte[] pContent)
	{
	    // |  ??????????????    |   ??????????????????	  |   ...{????????????}     |
	    // |  1 byte    |   2 bytes   |	  ...{??????????????????}   |

		int totalLen = 0;
		long checksum = 0;

		if (nLength<0 || nLength>MAX_PACKAGE_SIZE )
			return 0;
		if ( (nPackageType != CMD) && (nPackageType != DATA) && (nPackageType != ENDDATA) )
			return 0;
	    nLength+=2;
		pData[0] = (byte) nPackageType;        //??????????????????
		pData[1] = (byte) ((nLength>>8) & 0xff); //??????????????????????????
		pData[2] = (byte) (nLength & 0xff);      //??????????????????????????
		if (nLength>0)
		{
			for(int i=0;i<nLength;i++)
			{
				pData[3+i]=pContent[i];
			}
			//memcpy(pData+3, pContent, nLength);
		}

		totalLen = nLength + 3;

		return totalLen;
	}

	private int VerifyResponsePackage(byte nPackageType, byte[] pData)
	{
		long checkSum = 0;

		if ( pData[0] != nPackageType )
			return -3;

		int iLength;

		iLength = GetPackageLength(pData);  //??????????????????????????

		if (nPackageType == RESPONSE)
			return pData[3];  //??????????????

		return 0;
	}

	private void memset(byte[] pbuf,int size)
	{
		for(int i=0;i<size;i++)
		{
			pbuf[i]=0;
		}
	}

	private void memcpy(byte[] dstbuf,int dstoffset,byte[] srcbuf,int srcoffset,int size)
	{
		for(int i=0;i<size;i++)
		{
			dstbuf[dstoffset+i]=srcbuf[srcoffset+i];
		}
	}

	private int memcmp(byte[] dstbuf,byte[] srcbuf,int size)
	{
		for(int i=0;i<size;i++)
		{
			if(dstbuf[i]!=srcbuf[i])
				return -1;
		}
		return 0;
	}

	public int FPImageToBitmap(int imgType,byte[] pImageData,byte[] pBitmap)
	{
		memset(pBitmap,1078);
		pBitmap[0]=0x42;
		pBitmap[1]=0x4d;
		pBitmap[10]=0x36;
		pBitmap[11]=0x04;
		pBitmap[14]=0x28;
		pBitmap[26]=0x01;
		pBitmap[28]=0x08;

		pBitmap[18]= (byte) ((IMAGE_X>>0) & 0xFF);
		pBitmap[19]= (byte) ((IMAGE_X>>8) & 0xFF);
		pBitmap[20]= (byte) ((IMAGE_X>>16) & 0xFF);
		pBitmap[21]= (byte) ((IMAGE_X>>24) & 0xFF);
		pBitmap[22]= (byte) ((IMAGE_Y>>0) & 0xFF);
		pBitmap[23]= (byte) ((IMAGE_Y>>8) & 0xFF);
		pBitmap[24]= (byte) ((IMAGE_Y>>16) & 0xFF);
		pBitmap[25]= (byte) ((IMAGE_Y>>24) & 0xFF);

    	int j=0;
    	for (int i=54;i<1078;i=i+4)
    	{
    		pBitmap[i]=(byte) j;
    		pBitmap[i+1]=(byte) j;
    		pBitmap[i+2]=(byte) j;
    		pBitmap[i+3]=0;
    		j++;
    	}
    	bmpsize[0]=74806;
    	//memcpy(pBitmap,1078,pImageData,0,IMAGE_X*IMAGE_Y);
    	if(imgType==0){
    		bmpsize[0]= Constants.STDBMP_SIZE;
    		memcpy(pBitmap,1078,pImageData,0, Constants.STDIMAGE_X* Constants.STDIMAGE_Y);
    	}else{
    		bmpsize[0]= Constants.RESBMP_SIZE;
    		memcpy(pBitmap,1078,pImageData,0, Constants.RESIMAGE_X* Constants.RESIMAGE_Y);
    	}
		return 0;
	}
/////////////////////////////////////////////////////////////////////////////////
	public String FPErr2Str(int nErrCode)
	{
		String sErrorString;
			switch (nErrCode)
			{
			case -1:
			  sErrorString= "??????????????????????????????????";
			  break;
			case -2:
			  sErrorString= "??????????????????????????????????";
			  break;
			case -3:
			  sErrorString= "?????????????";
			  break;
			case -4:
			  sErrorString= "??????????????????????????????????????????????flash???????????????????";
			  break;
			case -5:
			  sErrorString= "????????????????????????";
			  break;
			case -6:
			  sErrorString= "????????????????????????????????????";
			  break;
			case -7:
			  sErrorString= "??????????????????????????????????";
			  break;
			case -8:
			  sErrorString= "??????????????????????????????";
			  break;
			case -9:
			  sErrorString= "?????????????????????????";
			  break;

			case 0:
			  sErrorString= "????????????";
			  break;
			case 1:
			  sErrorString= "??????????????????????????????????";
			  break;
			case 2:
			  sErrorString= "????????????????????????????????????????";
			  break;
			case 3:
			  sErrorString= "????????????????????????????????";
			  break;
			case 4:
			  sErrorString= "????????????????";
			  break;
			case 5:
			  sErrorString= "????????????????";
			  break;
			case 6:
			  sErrorString= "????????????????";
			  break;
			case 7:
			  sErrorString= "??????????????????????????????????";
			  break;
			case 8:
			  sErrorString= "??????????????????";
			  break;
			case 9:
			  sErrorString= "????????????????????????????";
			  break;
			case 10:
			  sErrorString= "????????????????????????????";
			  break;
			case 11:
			  sErrorString= "??????????????????????????????????";
			  break;
			case 12:
			  sErrorString= "????????????????????????????????????";
			  break;
			case 13:
			  sErrorString= "????????????????????????????";
			  break;
			case 14:
			  sErrorString= "????????????????????????????????????????????";
			  break;
			case 15:
			  sErrorString= "????????????????????????";
			  break;
			case 16:
			  sErrorString= "????????????????????????";
			  break;
			case 17:
			  sErrorString= "???????????????????????????";
			  break;
			case 18:
			  sErrorString= "????????????????????????????????";
			  break;
			case 19:
			  sErrorString= "????????????????????";
			  break;
			case 20:
			  sErrorString= "????????????????????";
			  break;
			case 21:
			  sErrorString= "????????????????????????";
			  break;
			case 22:
			  sErrorString= "????????????????????????????????";
			  break;
			case 23:
			  sErrorString= "????????????????????????????";
			  break;
			case 24:
			  sErrorString= "????????????????FLASH????????????";
			  break;
			case 25:
			  sErrorString= "???????????????????????";
			  break;
			case 26:
			  sErrorString= "????????????????????????????";
			  break;
			case 27:
			  sErrorString= "???????????????????????????????????????????";
			  break;
			case 28:
			  sErrorString= "??????????????????????????????????????????";
			  break;
			case 29:
			  sErrorString= "????????????????????????";
			  break;
			case 30:
			  sErrorString= "????????????????enroll??????????????";
			  break;
			case 31:
			  sErrorString= "????????????????";
			  break;
			case 0xf0:
			  sErrorString= "??????????????????????????????????????????????????????????????????????0xf0????????";
			  break;
			case 0xf1:
			  sErrorString= "???????????????????????????????????????????????????????????????0xf1????????";
			  break;
			case 0xf2:
			  sErrorString= "????????????????????????FLASH???????????????????????????";
			  break;
			case 0xf3:
			  sErrorString= "????????????????????????FLASH??????????????????????????????????";
			  break;
			case 0xf4:
			  sErrorString= "????????????????????????FLASH??????????????????????????????????";
			  break;
			case 0xf5:
			  sErrorString= "????????????????????????FLASH??????????????????????????????????";
			  break;
			case 0xf6:
			  sErrorString= "????????????????????????FLASH????????????????FLASH????????";
			  break;
			case 0x20:
			  sErrorString= "??????????????";
			  break;
			default:
			  sErrorString= "????????????????";
			  break;
			}

		return sErrorString;
	}
/////////////////////////////////////////////////////////////////////////////////
	public void SetInstance(Context parentContext){
    	pContext=parentContext;
    }

	public void SetContextHandler(Context parentContext, Handler handler){
    	pContext=parentContext;
    	mHandler=handler;
    }

	public int FPOpenDevice()
	{
		if(LibUSBOpen())
		{
			g_IsOpen=true;

			byte xpb[]=new byte[4];
			xpb[0]=0x78;
			xpb[1]=0x70;
			xpb[2]=0x62;
			xpb[3]=0x65;
			if(FPVfyDev(0xFFFFFFFF,xpb)==0){
				g_IsLink=true;
				g_bExit=false;
				return 0;
			}else{
				xpb[0]=0x78;
				xpb[1]=0x69;
				xpb[2]=0x61;
				xpb[3]=0x6f;
				if(FPVfyDev(0xFFFFFFFF,xpb)==0)	{
					g_IsLink=true;
					g_bExit=false;
					return 0;
				}
			}
			xpb[0]=0x00;
			xpb[1]=0x00;
			xpb[2]=0x00;
			xpb[3]=0x00;
			if(FPVfyDev(0xFFFFFFFF,xpb)==0)	{
				g_IsLink=true;
				g_bExit=false;
				return 0;
			}
			return -1;
		}else
			return -2;
	}

	public int FPCloseDevice()
	{
		if(LibUSBClose())
		{
			g_IsOpen=false;
			g_IsLink=false;
			return 0;
		}
		else
			return -1;
	}

	public int FPGetImage(int nAddr)
	{
		int num;
		byte cCmd[]=new byte[10];
		int result;
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];

		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = GET_IMAGE;  //??????????????????

	    num = FillPackage(iSendData, CMD, 1, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;

		if( !GetPackage(iGetData,64,TIME_OUT) )
			return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????

		return result;
	}

	public int FPGetImageEx(int nAddr)
	{
		int num;
		byte cCmd[]=new byte[10];
		int result;
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];

		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = GET_IMAGEEX;  //??????????????????

	    num = FillPackage(iSendData, CMD, 1, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;

		if( !GetPackage(iGetData,64,TIME_OUT) )
			return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????

		return result;
	}

	public int FPGenChar(int nAddr,int iBufferID)
	{
		byte cCmd[]=new byte[10];
		int num;
		int result;
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = GEN_CHAR;  //????????
		cCmd[1] = (byte) iBufferID;   //????????????????????????

	    num = FillPackage(iSendData, CMD, 2, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;
		if( !GetPackage(iGetData,64,TIME_OUT) )
			return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????

		return result;
	}

	public int FPGenCharEx(int nAddr,int iBufferID)
	{
		byte cCmd[]=new byte[10];
		int num;
		int result;
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = GEN_CHAREX;  //????????
		cCmd[1] = (byte) iBufferID;   //????????????????????????

	    num = FillPackage(iSendData, CMD, 2, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;
		if( !GetPackage(iGetData,64,TIME_OUT) )
			return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????

		return result;
	}

	public int	FPUpChar(int nAddr,int iBufferID, byte[] pTemplet, int[] iTempletLength)
	{
		int num;
		int result;
		byte cContent[]=new byte[10];
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		if( (iBufferID<1) || (iBufferID>3))
			return -4;

		cContent[0] = UP_CHAR;     //????????
		cContent[1] = (byte) iBufferID;

	    num = FillPackage(iSendData, CMD, 2, cContent);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;

		iTempletLength[0]=512;
		result=LibUSBGetData(pTemplet,512,TIME_OUT);

		return result;
	}

	public int	FPDownChar(int nAddr,int iBufferID, byte[] pTemplet, int iTempletLength)
	{
		int num;
		int result;
		byte cContent[]=new byte[10];
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		if( (iBufferID<1) || (iBufferID>3))
			return -4;

		cContent[0] = DOWN_CHAR;     //????????
		cContent[1] = (byte) iBufferID;

	    num = FillPackage(iSendData, CMD, 2, cContent); //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;

		return LibUSBDownData1(pTemplet,512);
	}

	public int	FPMatch(int nAddr,int[] iScore)
	{
		byte cCmd[]=new byte[10];
		int num;
		int result;
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = MATCH;     //????????

	    num = FillPackage(iSendData, CMD, 1, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;
		if( !GetPackage(iGetData,64,TIME_OUT) )
			return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????

		int hi=(iGetData[HEAD_LENGTH+1] >=0 ? iGetData[HEAD_LENGTH+1] : iGetData[HEAD_LENGTH+1] + 256);
		int lo=(iGetData[HEAD_LENGTH+2] >=0 ? iGetData[HEAD_LENGTH+2] : iGetData[HEAD_LENGTH+2] + 256);

		//iScore[0] = iGetData[HEAD_LENGTH+1]<<8;
		//iScore[0] |= iGetData[HEAD_LENGTH+2];

		iScore[0]=((hi<<8)&0xff00)+(lo);

		return result;
	}

	public int FPRegModule(int nAddr)
	{
		int num;
		int result;
		byte cCmd[]=new byte[10];
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = REG_MODULE;     //????????

	    num = FillPackage(iSendData, CMD, 1, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;
		if( !GetPackage(iGetData,64,TIME_OUT) )
			return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????
		return result;
	}

	public int	FPUpImage(int nAddr,byte[] pImageData, int[] iImageLength)
	{
		int num;
		int result;
		byte cCmd[]=new byte[10];
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = UP_IMAGE;     //????????

	    num = FillPackage(iSendData, CMD, 1, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;


		iImageLength[0]=IMAGE_X*IMAGE_Y;
		return LibUSBGetImage(pImageData,IMAGE_Y*IMAGE_X);
	}

	public int	FPUpImageEx(int nAddr,byte[] pImageData, int[] iImageLength)
	{
		int num;
		int result;
		byte cCmd[]=new byte[10];
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = UP_IMAGEEX;     //????????

	    num = FillPackage(iSendData, CMD, 1, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;


		iImageLength[0]= Constants.RESIMAGE_X* Constants.RESIMAGE_Y;
		return LibUSBGetImage(pImageData, Constants.RESIMAGE_Y* Constants.RESIMAGE_X);
	}

	public int FPSetPwd(int nAddr,byte[] pPassword)
	{
		int num;
		int result;
		byte cContent[]=new byte[10];
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cContent[0] = SET_PWD;     //????????
		cContent[1] = pPassword[0];
		cContent[2] = pPassword[1];
		cContent[3] = pPassword[2];
		cContent[4] = pPassword[3];

		num = FillPackage(iSendData, CMD, 5, cContent);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
		  return -1;
		if( !GetPackage(iGetData,64,1000) )
		  return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????

		return result;
	}

	public int FPVfyDev(int nAddr,byte[] pPassword)
	{
		int num;
		int result;
		byte cContent[]=new byte[10];
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cContent[0] = VFY_PWD;     //????????
		cContent[1] = pPassword[0];
		cContent[2] = pPassword[1];
		cContent[3] = pPassword[2];
		cContent[4] = pPassword[3];

		num = FillPackage(iSendData, CMD, 5, cContent);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
		  return -1;
		if( !GetPackage(iGetData,64,1000) )
		  return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????

		return result;
	}

	public int FPReadInfo(int nAddr,int nPage,byte[] UserContent)
	{
		int num;
		int result=0;
		byte cContent[]=new byte[10];
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);


		cContent[0] = READ_NOTEPAD;
		cContent[1] = (byte) nPage;

		num = FillPackage(iSendData, CMD, 2, cContent);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
		  return -1;

		if( !GetPackage(iGetData,64,TIME_OUT) )
		  return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????
		if(result!=0)
		  return result;
		memcpy(UserContent,0,iGetData,HEAD_LENGTH+1,32);

		return result;
	}

	public int FPOpenDoor(int nAddr,int iSwitch)
	{
		byte cCmd[]=new byte[10];
		int num;
		int result;
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = OPENDOOR;  //????????
		cCmd[1] = (byte) iSwitch;   //????????????????????????

	    num = FillPackage(iSendData, CMD, 2, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;
		if( !GetPackage(iGetData,64,TIME_OUT) )
			return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????

		return result;
	}

	public int FPReadCardSn(int nAddr,byte[] cardsn)
	{
		byte cCmd[]=new byte[10];
		int num;
		int result;
		byte iSendData[]=new byte[MAX_PACKAGE_SIZE];
		byte iGetData[]=new byte[MAX_PACKAGE_SIZE];
		memset(iSendData,MAX_PACKAGE_SIZE);
		memset(iGetData,MAX_PACKAGE_SIZE);

		cCmd[0] = READCARDSN;  //????????
	    num = FillPackage(iSendData, CMD, 1, cCmd);  //???????????????????????

		if( !SendPackage(nAddr,iSendData) )
			return -1;
		if( !GetPackage(iGetData,64,TIME_OUT) )
			return -2;

		result = VerifyResponsePackage(RESPONSE, iGetData);  //???????????????????
		if(result!=0)
			  return result;
		memcpy(cardsn,0,iGetData,HEAD_LENGTH+1,5);
		return result;
	}

	public int FxGetImage(int imgType,int nAddr){
    	if(imgType==0){
    		return FPGetImage(nAddr);
    	}else{
    		return FPGetImageEx(nAddr);
    	}
    }

    public int FxGenChar(int imgType,int nAddr,int iBufferID){
    	if(imgType==0){
    		return FPGenChar(0xffffffff,iBufferID);
    	}else{
    		return FPGenCharEx(0xffffffff,iBufferID);
    	}
    }

    public int	FxUpImage(int imgType,int nAddr,byte[] pImageData, int[] iImageLength){
    	if(imgType==0){
    		return FPUpImage(nAddr,pImageData,iImageLength);
    	}else{
    		return FPUpImageEx(nAddr,pImageData,iImageLength);
    	}
    }

	public int OpenDevice()
	{
		return FPOpenDevice();
	}

	public int CloseDevice()
	{
		return FPCloseDevice();
	}


    public int MatchTemplate(byte[] ptep1dat,int ptep1size,byte[] ptep2dat,int ptep2size)
    {
    	int matval[]=new int[1];
    	if(FPDownChar(0xFFFFFFFF,1,ptep1dat,ptep1size)==0)
    	{
    		if(FPDownChar(0xFFFFFFFF,2,ptep2dat,ptep2size)==0)
    		{
				int ret=FPMatch(0xFFFFFFFF,matval);
				if(ret==0)
					return matval[0];
				else
					return 0;
    		}
    	}
		return 0;
    }

   //public native void GetLinkInfo(int itype,byte[] info);
   //static {
   //   System.loadLibrary("fgtitinit");
   //}
}
