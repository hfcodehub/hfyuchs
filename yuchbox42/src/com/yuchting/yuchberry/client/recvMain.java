/**
 *  Dear developer:
 *  
 *   If you want to modify this file of project and re-publish this please visit:
 *  
 *     http://code.google.com/p/yuchberry/wiki/Project_files_header
 *     
 *   to check your responsibility and my humble proposal. Thanks!
 *   
 *  -- 
 *  Yuchs' Developer    
 *  
 *  
 *  
 *  
 *  尊敬的开发者：
 *   
 *    如果你想要修改这个项目中的文件，同时重新发布项目程序，请访问一下：
 *    
 *      http://code.google.com/p/yuchberry/wiki/Project_files_header
 *      
 *    了解你的责任，还有我卑微的建议。 谢谢！
 *   
 *  -- 
 *  语盒开发者
 *  
 */
package com.yuchting.yuchberry.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemListener;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import local.yblocalResource;
import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.blackberry.api.menuitem.ApplicationMenuItemRepository;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.notification.NotificationsConstants;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;

import com.yuchting.yuchberry.client.connectDeamon.FetchAttachment;
import com.yuchting.yuchberry.client.im.IMStatus;
import com.yuchting.yuchberry.client.im.MainIMScreen;
import com.yuchting.yuchberry.client.im.fetchChatRoster;
import com.yuchting.yuchberry.client.screen.ChangeMailSenderDlg;
import com.yuchting.yuchberry.client.screen.PhizSelectedScreen;
import com.yuchting.yuchberry.client.screen.aboutScreen;
import com.yuchting.yuchberry.client.screen.audioViewScreen;
import com.yuchting.yuchberry.client.screen.imageViewScreen;
import com.yuchting.yuchberry.client.screen.settingScreen;
import com.yuchting.yuchberry.client.screen.shareYBScreen;
import com.yuchting.yuchberry.client.screen.stateScreen;
import com.yuchting.yuchberry.client.screen.textViewScreen;
import com.yuchting.yuchberry.client.screen.uploadFileScreen;
import com.yuchting.yuchberry.client.screen.videoViewScreen;
import com.yuchting.yuchberry.client.ui.ImageSets;
import com.yuchting.yuchberry.client.ui.ImageUnit;
import com.yuchting.yuchberry.client.ui.Phiz;
import com.yuchting.yuchberry.client.ui.WeiboHeadImage;
import com.yuchting.yuchberry.client.weibo.WeiboAccount;
import com.yuchting.yuchberry.client.weibo.fetchWeibo;
import com.yuchting.yuchberry.client.weibo.weiboTimeLineScreen;



public class recvMain extends UiApplication implements yblocalResource,LocationListener {
	
	public final static int 		fsm_display_width		= Display.getWidth();
	public final static int 		fsm_display_height		= Display.getHeight();
	public final static String	fsm_OS_version			= CodeModuleManager.getModuleVersion((CodeModuleManager.getModuleHandleForObject("")));
	public final static String	fsm_client_version		= ApplicationDescriptor.currentApplicationDescriptor().getVersion();
	public final static long		fsm_PIN					= DeviceInfo.getDeviceId();
	public final static String	fsm_IMEI				= "bb";
	
	public final static boolean  fsm_snapshotAvailible = Float.valueOf(recvMain.fsm_OS_version.substring(0,3)).floatValue() > 4.5f;
	
	public static int				fsm_delayLoadingTime	= 600;
	static{
		
		if(DeviceInfo.isSimulator()){
			fsm_delayLoadingTime = 100;
		}else{
			if(fsm_OS_version.startsWith("7.")){
				fsm_delayLoadingTime = 300;
			}else if(fsm_OS_version.startsWith("6.")){
				fsm_delayLoadingTime = 600;
			}else if(fsm_OS_version.startsWith("5.")){
				fsm_delayLoadingTime = 800;
			}else{
				fsm_delayLoadingTime = 1000;
			}
		}
		
	}
	
	public final static ResourceBundle sm_local = ResourceBundle.getBundle(yblocalResource.BUNDLE_ID, yblocalResource.BUNDLE_NAME);
	
	final static long		fsm_notifyID_email = 767918509114947L;
	
	final static Object 	fsm_notifyEvent_email = new Object() {
	    public String toString() {
	       return recvMain.sm_local.getString(yblocalResource.NOTIFY_EMAIL_LABEL);
	    }
	};
	
	final static long		fsm_notifyID_weibo = 767918509114948L;
	
	final static Object 	fsm_notifyEvent_weibo = new Object() {
	    public String toString() {
	       return recvMain.sm_local.getString(yblocalResource.NOTIFY_WEIBO_LABEL);
	    }
	};
	
	
	final static long		fsm_notifyID_disconnect = 767918509114949L;
	
	final static Object 	fsm_notifyEvent_disconnect = new Object() {
	    public String toString() {
	       return recvMain.sm_local.getString(yblocalResource.NOTIFY_DISCONNECT_LABEL);
	    }
	};
	
	final static long		fsm_notifyID_weibo_home = 767918509114950L;
	
	final static Object 	fsm_notifyEvent_weibo_home = new Object() {
	    public String toString() {
	       return recvMain.sm_local.getString(yblocalResource.NOTIFY_WEIBO_LABEL_HOME);
	    }
	};
	
	final static long		fsm_notifyID_im = 767918509114951L;
	
	final static Object 	fsm_notifyEvent_im = new Object() {
	    public String toString() {
	       return recvMain.sm_local.getString(yblocalResource.NOTIFY_IM_LABEL);
	    }
	};
	
	final static long		fsm_notifyID_email_failed = 767918509114952L;
	
	final static Object 	fsm_notifyEvent_email_failed = new Object() {
	    public String toString() {
	       return recvMain.sm_local.getString(yblocalResource.NOTIFY_EMAIL_FAILED_LABEL);
	    }
	};
	
final static long		fsm_notifyID_net_broken = 767918509114953L;
	
	final static Object 	fsm_notifyEvent_net_broken = new Object() {
	    public String toString() {
	       return recvMain.sm_local.getString(yblocalResource.NOTIFY_NET_BROKEN_LABEL);
	    }
	};
	
	
	public connectDeamon 		m_connectDeamon		= new connectDeamon(this);
	
	public aboutScreen			m_aboutScreen		= null;
	public stateScreen 			m_stateScreen 		= null;
	public debugInfo			m_debugInfoScreen	= null;
	public downloadDlg			m_downloadDlg		= null;
	public settingScreen		m_settingScreen		= null;
	public shareYBScreen		m_shareScreen		= null;
			
	UiApplication				m_messageApplication = null;
	
	public final static	int				DISCONNECT_STATE = 0;
	public final static	int				CONNECTING_STATE = 1;
	public final static	int				CONNECTED_STATE = 2;
	
	
	public int					m_connectState		= 0; 
	public String				m_aboutString		= recvMain.sm_local.getString(yblocalResource.ABOUT_DESC);
	
	final class ErrorInfo{
		Date		m_time;
		String		m_info;
		
		ErrorInfo(String _info){
			m_info	= _info;
			m_time	= new Date();
		}
	}
	
	public Vector			m_errorString		= new Vector();	
	public Vector			m_uploadingDesc 	= new Vector();
	
	public String			m_hostname 			= "";
	public int				m_port 				= 0;
	public String			m_account			= "";
	public String			m_userPassword 		= "";
	public boolean			m_useSSL			= false;
	public boolean			m_useWifi			= false;
	public boolean			m_useMDS			= false;
	public String			m_carrier			= "";
	public boolean			m_autoRun			= false;	
	
	public boolean			m_discardOrgText	= false;
	public boolean			m_delRemoteMail		= false;
	public boolean			m_markReadMailInSvr	= true;
	public boolean			m_popupDlgWhenComposeNew = false;
	public boolean			m_mailHtmlShow		= false;
	public boolean			m_mailHtmlShowOnlyWIFI = false;
	
	public final class APNSelector{
		public String		m_name			= null;
		public int			m_validateNum	= 0;
		
		public APNSelector(){}
		
		public APNSelector(String _name){
			m_name = _name;
		}
	}
	
	public Vector				m_APNList 			= new Vector();
	public int					m_currentAPNIdx 	= 0;
	public int					m_changeAPNCounter 	= 0;
	public String				m_appendString		= "";
	
	public long				m_uploadByte		= 0;
	public long				m_downloadByte		= 0;
	
	public int					m_sendMailNum		= 0;
	public int					m_recvMailNum		= 0;
	public String				m_passwordKey		= "";
	
	public boolean				m_closeMailSendModule = false;
	
	public boolean			m_connectDisconnectPrompt = false;
	public boolean			m_popupDlgWhenDisconnect = false;
	
	
	public static final String[]	fsm_recvMaxTextLenghtString = {"∞","1KB","5KB","10KB","50KB"};
	public static final int[]		fsm_recvMaxTextLenght		= {0,1024,1024*5,1024*10,1024*50};
	public int						m_recvMsgTextLengthIndex = 0;
	
	
	public static final String[]	fsm_pulseIntervalString = {"1","3","5","10","30"};
	public static final int[]		fsm_pulseInterval		= {1,3,5,10,30};
	public int						m_pulseIntervalIndex = 2;
	
	public static final String[] fsm_apnListString = 
	{
		sm_local.getString(yblocalResource.APN_LIST_NULL),
		sm_local.getString(yblocalResource.APN_LIST_CMNET) 	+ " cmnet",
		sm_local.getString(yblocalResource.APN_LIST_UNINET) 	+ " uninet",
		sm_local.getString(yblocalResource.APN_LIST_3GNET) 	+ " 3gnet",
		sm_local.getString(yblocalResource.APN_LIST_CTNET) 	+ " ctnet",
	};
	
	public static final String[]		fsm_apnString		= 
	{
		"",
		"cmnet",
		"uninet",
		"3gnet",
		"ctnet",
	};
	
	public int				m_apnStringIndex 	= 0;
	
	public boolean			m_fulldayPrompt		= true;
	public int				m_startPromptHour	= 8;
	public int				m_endPromptHour		= 22;
	
	public boolean			m_copyMailToSentFolder = false;
	
	public Vector 			m_sendMailAccountList = new Vector();
	public int				m_defaultSendMailAccountIndex = 0;
	public int				m_defaultSendMailAccountIndex_tmp = -1;
	
	public boolean 		m_hideBackgroundIcon = false;
	
	//! flurry agent key
	private String			m_flurryKey			= null;
	
	public final class UploadingDesc{
		
		public fetchMail		m_mail = null;
		public int				m_attachmentIdx;
		public int				m_uploadedSize;
		public int				m_totalSize;		
	}
		
	ApplicationMenuItem m_addItem	= new ApplicationMenuItem(20){
						
		public String toString(){
			return recvMain.sm_local.getString(yblocalResource.ADD_ATTACHMENT);
		}
		
		public Object run(Object context){
			if(context instanceof Message ){
				return OpenAttachmentFileScreen(context,false);
			}
			
			return context;
		}
	};
	
	ApplicationMenuItem	m_delItem	= new ApplicationMenuItem(21){
		public String toString(){
			return recvMain.sm_local.getString(yblocalResource.CHECK_DEL_ATTACHMENT);
		}
		
		public Object run(Object context){
			if(context instanceof Message ){

				return OpenAttachmentFileScreen(context,true);
			}
			
			return context;	
		}
	};
	
	ApplicationMenuItem	m_changeDefaultSenderItem	= new ApplicationMenuItem(22){
		public String toString(){
			return recvMain.sm_local.getString(yblocalResource.CHANGE_DEFAULT_SENDER_ACC);
		}
		
		public Object run(Object context){
			if(context instanceof Message ){
				return openChangeMailSenderDlg(context);
			}
			
			return context;	
		}
	};
		
	String m_latestVersion			= null;
	
	//@{ location information
	public LocationProvider m_locationProvider = null;
	public boolean		 m_useLocationInfo = false;
	public boolean		 m_setLocationListener = false;
	
	public GPSInfo			m_gpsInfo = new GPSInfo();
	//@}
	
	public boolean		m_mailUseLocation = false;
	
	public ImageSets	m_allImageSets 		= null;
	
	FileConnection m_logfc				= null;
	OutputStream	m_logfcOutput		= null;
				
	public static void main(String[] args) {	
		recvMain t_theApp = new recvMain(ApplicationManager.getApplicationManager().inStartup());
		t_theApp.enterEventDispatcher();
	}
	
	public boolean		canUseLocation(){
		return m_useLocationInfo;
	}
	
	public GPSInfo getGPSInfo(){
		return m_gpsInfo;
	}
	
	public int getMaxWeiboNum(){
		if(m_maxWeiboNumIndex >= 0 && m_maxWeiboNumIndex < fsm_maxWeiboNum.length){
			return fsm_maxWeiboNum[m_maxWeiboNumIndex];
		}
		
		return fsm_maxWeiboNum[0];
	}
	
	private void makeDir(String _path)throws Exception{
		FileConnection fc = (FileConnection) Connector.open(_path,Connector.READ_WRITE);
		try{
			if(!fc.exists()){
				fc.mkdir();
			}
		}finally{
			fc.close();
			fc = null;
		}
	}
	
	public recvMain(boolean _systemRun){
		
		try{
			m_allImageSets = new ImageSets("/state_images.imageset");
    	}catch(Exception e){
    		DialogAlertAndExit("load state_images error:"+e.getMessage()+e.getClass().getName());
    		return ;
    	}
    	
		try{
			makeDir(uploadFileScreen.fsm_rootPath_back + "YuchBerry/");			
		}catch (Exception _e) {
			DialogAlertAndExit(sm_local.getString(yblocalResource.SYSTEM_ROM_ERROR));
        	return;
		}
		
		if(!_systemRun){
			try{
				makeDir(uploadFileScreen.fsm_rootPath_default + "YuchBerry/");				
			}catch (Exception _e) {
				// if in system run, the SDCard is never used
				// or without SDCard
			}
		}
		
		
		GetAttachmentDir();
		        
        Criteria t_criteria = new Criteria();
		t_criteria.setCostAllowed(false);
		t_criteria.setHorizontalAccuracy(50);
		t_criteria.setVerticalAccuracy(50);
		
		try{
			m_locationProvider = LocationProvider.getInstance(t_criteria);
			if(m_locationProvider == null){
				SetErrorString(sm_local.getString(yblocalResource.SYSTEM_GPS_ERROR));
			}
		}catch(Exception e){
			SetErrorString("location:"+e.getMessage()+" " + e.getClass().getName());
		}     
        
		// must read the configure first
		//
		WriteReadIni(true);
		
		// prepare the APN
		//
		prepareAPN();
				
		// register the notification
    	//
    	NotificationsManager.registerSource(fsm_notifyID_email, fsm_notifyEvent_email,NotificationsConstants.CASUAL);
    	NotificationsManager.registerSource(fsm_notifyID_weibo, fsm_notifyEvent_weibo,NotificationsConstants.CASUAL);
    	NotificationsManager.registerSource(fsm_notifyID_weibo_home, fsm_notifyEvent_weibo_home,NotificationsConstants.CASUAL);
    	NotificationsManager.registerSource(fsm_notifyID_disconnect, fsm_notifyEvent_disconnect,NotificationsConstants.CASUAL);
    	NotificationsManager.registerSource(fsm_notifyID_net_broken, fsm_notifyEvent_net_broken,NotificationsConstants.CASUAL);
    	NotificationsManager.registerSource(fsm_notifyID_im, fsm_notifyEvent_im,NotificationsConstants.CASUAL);
    	NotificationsManager.registerSource(fsm_notifyID_email_failed, fsm_notifyEvent_email_failed,NotificationsConstants.CASUAL);
    	    	    	
        if(_systemRun){       
        	
        	if(!m_autoRun || m_hostname.length() == 0 || m_port == 0 || m_userPassword.length() == 0){
        		System.exit(0);
        	}else{
        		try{
        			m_connectDeamon.Connect();
        			Start();
        		}catch(Exception e){
        			System.exit(0);
        		}
        	}      	
        }
        
        // initialize flurry
        initFlurry();
        
        addFileSystemListener(new FileSystemListener(){
        	public void rootChanged(int state,String rootName) {
        		if( state == ROOT_ADDED ) {
        			if(rootName.equalsIgnoreCase("sdcard/") ) {
        				//microSD card inserted
        				synchronized (recvMain.this) {
							m_isSDCardAvailable = true;
						}
        			}
        		}else if(state == ROOT_REMOVED) {
        			if(rootName.equalsIgnoreCase("sdcard/") ) {
        				//microSD card inserted
        				synchronized (recvMain.this) {
							m_isSDCardAvailable = false;
						}
        			}
		        }
        	}
        });
        
        isSDCardAvailable(true);
        
        // for the add-on
        //
        WeiboHeadImage.sm_mainApp = this;
        InitWeiboModule();
        initIMModule();
	}
	
	/**
	 * intialize the flurry
	 */
	private void initFlurry(){
		
//		try{
//			InputStream t_file = getClass().getResourceAsStream("/FlurryKey.txt");
//			try{
//
//				ByteArrayOutputStream os = new ByteArrayOutputStream();
//				try{
//					int t_readByte;
//					while((t_readByte = t_file.read()) != -1){
//						os.write(t_readByte);
//					}
//					m_flurryKey = new String(os.toByteArray());
//					
//					// invoke later to make sure flurry run in YuchCaller context
//					invokeLater(new Runnable() {
//						public void run() {
//							FlurryAgent.onStartApp(m_flurryKey);
//						}
//					});
//					
//					// invoke a runnable for destory app every 6 hours for sending custom event
//					// check follow URL for detail
//					// http://supportforums.blackberry.com/t5/Java-Development/Create-Event-in-Flurry-Analytics/td-p/1951539
//					//
//					invokeLater(new Runnable() {
//						
//						public void run() {							
//							// emulate destory app
//							FlurryAgent.onDestroyApp();
//							
//							// restart again after 20 second
//							invokeLater(new Runnable() {		
//								public void run() {
//									FlurryAgent.onStartApp(m_flurryKey);
//								}
//							},20000,false);
//						}
//					},6 * 3600000,true);
//					
//				}finally{
//					os.close();
//				}				
//			}finally{
//				t_file.close();
//			}			
//		}catch(Exception e){
//			System.out.println("Flurry init failed!"+e.getMessage());
//		}
	}
	
	private void endFlurry(){
//		if(m_flurryKey != null){
//			FlurryAgent.onDestroyApp();
//		}
	}
	
	/**
	 * prepare the APN value
	 */
	private void prepareAPN(){
		try{
			String t_carrierName = RadioInfo.getCurrentNetworkName();
			
			if(t_carrierName != null && (!t_carrierName.equals(m_carrier) || m_APNList.isEmpty())){
				
				String t_apn = findNetworkAPN();
				
				m_carrier = t_carrierName;
				
				if(t_apn != null){
					boolean t_found = false;
					for(int i = 0;i< m_APNList.size();i++){
						
						APNSelector apn = (APNSelector)m_APNList.elementAt(i);
						
						if(apn.m_name.equalsIgnoreCase(t_apn)){
							t_found = true;
							break;
						}
					}
					
					if(!t_found){
						m_APNList.addElement(new APNSelector(t_apn));
					}
					
				}
				
				SetErrorString("Carrier["+m_carrier+"] APN["+t_apn+"]");
			}			
			
		}catch(Exception e){
			SetErrorString("RADIO:", e);
		}
	}
	
	/**
	 * find a good avaiable APN
	 * @return
	 * @throws Exception
	 */
	public static String findNetworkAPN(){
		
		String t_carrierName = RadioInfo.getCurrentNetworkName();
		
		String t_apn = null;
		if(t_carrierName.equals("中国移动") || t_carrierName.toLowerCase().equals("china mobile")){
			t_apn = "cmnet";
		}else if(t_carrierName.equals("中国联通") || t_carrierName.toLowerCase().equals("china unicom")){
			t_apn = "uninet";
		}else if(t_carrierName.equals("中国电信") || t_carrierName.toLowerCase().equals("china telecom")){
			t_apn = "ctnet";
		}
		
		return t_apn;
	}
	
	protected boolean acceptsForeground() {
		if(!m_hideBackgroundIcon){
			return true; 
		}
		try{
			return Application.getApplication() instanceof recvMain;
		}catch(Exception e){
			return false;
		}
		
	}
	
	public boolean isBackground(){
		return !isForeground();
	}
	
	private boolean m_initWeiboHeadImageDir = false;
	public final static String fsm_weiboImageDir = "YuchBerry/WeiboImage/";
	public String[]				m_weiboHeadImageDir_sub = 
	{
		"Sina/","TW/","QQ/",		
		"163/","SOHU/","FAN/",
	};
	
	public String GetWeiboHeadImageDir(int _style)throws Exception{
		
		mkHeadImageDir(uploadFileScreen.fsm_rootPath_default + fsm_weiboImageDir,
						m_weiboHeadImageDir_sub,m_initWeiboHeadImageDir);
		
		m_initWeiboHeadImageDir = true;
		
		return m_weiboHeadImageDir_sub[_style];
	}
	
	private boolean m_initIMHeadImageDir = false;
	public final static String fsm_IMImageDir = "YuchBerry/IMImage/";
	public String[]				m_IMHeadImageDir_sub = 
	{
		"GTalk/","MSN/"
	};
	
	public String GetIMHeadImageDir(int _style)throws Exception{
		
		mkHeadImageDir(uploadFileScreen.fsm_rootPath_default + fsm_IMImageDir,
						m_IMHeadImageDir_sub,m_initIMHeadImageDir);
		
		m_initIMHeadImageDir = true;
		
		return m_IMHeadImageDir_sub[_style];
	}
	
	private void mkHeadImageDir(String _prefix,String[] dir,boolean _init)throws Exception{
		
		if(!isSDCardAvailable(false)){
			throw new Exception("Can't use the sd card to store weibo head image.");
		}
		
		// connect the string of head image directory
		//
		if(!_init){
			
			makeDir(uploadFileScreen.fsm_rootPath_default + "YuchBerry/");	
			
        	for(int i = 0;i < dir.length;i++){
        		dir[i] = _prefix + dir[i];
        	}
        	        	
    		// create the sdcard path 
    		//
        	makeDir(_prefix);
        	
        	// attempt create the head image directory
    		//
    		for(int i = 0;i < dir.length;i++){
    			makeDir(dir[i]);
        	}
    	}		
	}
	
	boolean m_isSDCardAvailable = false;
	
	public boolean isSDCardAvailable(boolean _force){
				
		if(_force){
			
			synchronized (this) {
				m_isSDCardAvailable = false;
			}
			
			String root = null;
			Enumeration e = FileSystemRegistry.listRoots();
			while (e.hasMoreElements()) {
			     root = (String) e.nextElement();
			     if( root.equalsIgnoreCase("sdcard/") ) {
			    	 synchronized (this) {
			    		 m_isSDCardAvailable = true;
			    	 }
			    	 break;
			     }
			}
		}
		
		return m_isSDCardAvailable;
	}
	
	public final static String fsm_mailAttachDir = "YuchBerry/AttDir/";
	
	public String GetAttachmentDir(){
		
		boolean t_sdCard = isSDCardAvailable(false);
		
		String t_attDir = (t_sdCard?uploadFileScreen.fsm_rootPath_default:uploadFileScreen.fsm_rootPath_back) + fsm_mailAttachDir;
		try{
			if(t_sdCard){
				makeDir(uploadFileScreen.fsm_rootPath_default + "YuchBerry/");
			}
			
			makeDir(t_attDir);
			
		}catch(Exception e){
			DialogAlertAndExit("create AttDir failed: " + t_attDir);
			t_attDir = "";
		}
				
		return t_attDir;
	}
	
	public void DialogAlertAndExit(final String _msg) {

		invokeLater(new Runnable() {
			public void run() {
				synchronized(getEventLock()) {
					Dialog.alert(_msg);
					
					endFlurry();	
					System.exit(0);
				}
			}
		});
	
	}
	
	public void SetReportLatestVersion(String _latestVersion){
		m_latestVersion = _latestVersion;
		
		if(m_latestVersion != null){
			
			if(m_stateScreen != null){
				PopupLatestVersionDlg();
			}			
		}
	}
	
	private void PopupLatestVersionDlg(){
		
		if(m_latestVersion != null){
						
			Dialog t_dlg = new Dialog(Dialog.D_OK_CANCEL,sm_local.getString(yblocalResource.LATEST_VER_REPORT) + m_latestVersion,
					Dialog.OK,Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION),Manager.VERTICAL_SCROLL);
			
			t_dlg.setDialogClosedListener(new DialogClosedListener(){
			
				public void dialogClosed(Dialog dialog, int choice) {
					
					switch (choice) {
						case Dialog.OK:
							openURL("http://ota.yuchs.com/");
							
							break;
						
						default:
							break;
					}
				}
			});
			
			t_dlg.setEscapeEnabled(true);
			synchronized (getEventLock()) {
				pushGlobalScreen(t_dlg,1, UiEngine.GLOBAL_QUEUE);
			}
			
			m_latestVersion = null;
		}
	}
	
	public String GetAPNName(){
		
		if(++m_changeAPNCounter > 3){
			m_changeAPNCounter = 0;
			
			if(++m_currentAPNIdx >= m_APNList.size()){
				m_currentAPNIdx = 0;
			}
		}		
		
		if(m_currentAPNIdx < m_APNList.size()){
			return ((APNSelector)m_APNList.elementAt(m_currentAPNIdx)).m_name;
		}
		
		return "";
	}
	
	public String GetAPNList(){
		
		if(!m_APNList.isEmpty()){
			String t_str = ((APNSelector)m_APNList.elementAt(0)).m_name;
			
			for(int i = 1;i < m_APNList.size();i++){
				APNSelector t_sel = (APNSelector)m_APNList.elementAt(i); 
				t_str = t_str + ";" + t_sel.m_name;
			}
			
			return t_str;
		}		
		
		return "";
	}
	
	// check follow URL for detail
	// http://www.blackberry.com/knowledgecenterpublic/livelink.exe/fetch/2000/348583/800332/1295814/How_To_-_Programmatically_determine_if_a_microSD_card_has_been_inserted.html?nodeid=1295868&vernum=0
	//
	public static boolean isSDCardSupport(){
		String modelNum = DeviceInfo.getDeviceName();
		if ((modelNum.startsWith("8") && !modelNum.startsWith("87")) || modelNum.startsWith("9") || modelNum.startsWith("10")) {
			return true;
		}
				
		return false;
	}
	
	public boolean UseWifiConnection(){
		
		// 4.2
		// return false;
		
//		int stat = WLANInfo.getWLANState();
//		boolean t_connect = WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED;
//		t_connect = WLANInfo.getAPInfo() != null;
//		t_connect = WLANInfo.getWLANState() != WLANInfo.WLAN_STATE_DISCONNECTED; 
//		t_connect = CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT,RadioInfo.WAF_WLAN, false);
//		
//		if(m_useWifi && WLANInfo.getAPInfo() != null){
//			SetErrorString("Using wifi to connect");
//			return true;
//		}
		
		return false;
	}
	
	public int GetRecvMsgMaxLength(){
		return fsm_recvMaxTextLenght[m_recvMsgTextLengthIndex];
	}
	
	public void SetAPNName(String _APNList){
		
		m_APNList.removeAllElements();
		
		int t_beginIdx = 0;
		int t_endIdx = -1;
		
		do{
			t_endIdx = _APNList.indexOf(';',t_beginIdx);
			
			if(t_endIdx != -1){
				String t_name = _APNList.substring(t_beginIdx, t_endIdx);
				if(t_name.length() != 0){
					APNSelector t_sel = new APNSelector(t_name);
					m_APNList.addElement(t_sel);
				}
				
			}else{
				String t_name = _APNList.substring(t_beginIdx, _APNList.length());
				if(t_name.length() != 0){
					APNSelector t_sel = new APNSelector(t_name);
					m_APNList.addElement(t_sel);
				}
				break;
			}
			
			t_beginIdx = t_endIdx + 1;
			
		}while(t_beginIdx < _APNList.length());
		
	}
	
	public String GetURLAppendString(){

		String t_result = "";
		
		if(UseWifiConnection()){
			
			t_result = ";interface=wifi";
			
		}else{
			
			String t_APN = GetAPNName();
			
			if(t_APN.length() != 0){
				t_result = ";apn=" + t_APN;			
			}
		
			if(m_appendString.length() != 0){
				
				final String t_replaceSign = "$apn$";
				
				final int t_replaceIdx = m_appendString.indexOf(t_replaceSign); 
				if( t_replaceIdx != -1 && t_APN.length() != 0){
					t_result = t_result + ";" + m_appendString.substring(0,t_replaceIdx) + t_APN + m_appendString.substring(t_replaceIdx + t_replaceSign.length());
				}else{
					t_result = t_result + ";" + m_appendString;
				}
			}
		}		
		 
		return t_result;
	}
	
	public int GetSendMailNum(){return m_sendMailNum;}
	public void SetSendMailNum(int _num){
		m_sendMailNum = _num;
		
		if(m_settingScreen != null){
			m_settingScreen.RefreshUpDownloadByte();
		}
	}
	
	public int GetRecvMailNum(){return m_recvMailNum;}
	public void SetRecvMailNum(int _num){
		m_recvMailNum = _num;
		
		if(m_settingScreen != null){
			m_settingScreen.RefreshUpDownloadByte();
		}
	}	
	
	public boolean IsPromptTime(){
		
		if(!m_fulldayPrompt){
			SimpleDateFormat t_format = new SimpleDateFormat("HH");
			final int t_hour = Integer.valueOf(t_format.format(new Date())).intValue();
			return t_hour >= m_startPromptHour && t_hour + 1 <= m_endPromptHour;
		}
		return true;
	}
	public String GetHostName(){
		return m_hostname;
	}
	
	public int GetHostPort(){
		return m_port;
	}
	
	public String GetUserPassword(){
		return m_userPassword;
	}
	
	public boolean IsUseSSL(){
		return m_useSSL;
	}
	
	public boolean UseMDS(){
		return m_useMDS;
	}
	
	public int GetPulseIntervalMinutes(){
		return fsm_pulseInterval[m_pulseIntervalIndex];
	}
	
	
//	static public synchronized void Copyfile(String _from,String _to)throws Exception{
//		
//		byte[] t_buffer = null;
//		
//		FileConnection t_fromFile = (FileConnection) Connector.open(_from,Connector.READ_WRITE);
//		try{
//			if(t_fromFile.exists()){
//
//				t_buffer = new byte[(int)t_fromFile.fileSize()];
//				InputStream t_readFile = t_fromFile.openInputStream();
//				try{
//					t_readFile.read(t_buffer);
//				}finally{
//					
//					t_readFile.close();
//					t_readFile = null;
//				}
//								
//			}else{
//				return ;
//			}
//			
//		}finally{		
//			
//			t_fromFile.close();
//			t_fromFile = null;
//		}	
//		
//		
//		FileConnection t_toFile  = (FileConnection) Connector.open(_to,Connector.READ_WRITE);
//		try{
//			if(!t_toFile.exists()){
//				t_toFile.create();
//			}
//			
//			OutputStream t_writeFile = t_toFile.openOutputStream();
//			try{
//				t_writeFile.write(t_buffer);
//			}finally{
//				t_writeFile.close();
//				t_writeFile = null;							
//			}
//		}finally{
//			t_toFile.close();
//			t_toFile = null;
//		}		
//	}
	
	/**
	 * pre process write or read a file
	 * 
	 * Write: change original name to back file name when write
	 * 
	 * Read: change back filename to original filename if back file is existed
	 */
	private void preWriteReadIni(boolean _read,String _pathFilename){
		
		try{
			
			int tSlashIdx = _pathFilename.lastIndexOf('/');
			
			String tParentPath	= _pathFilename.substring(0,tSlashIdx + 1);
			String tOrgFilename = _pathFilename.substring(tSlashIdx + 1);
			
			String tBackFilename		= "~" + tOrgFilename;
			String tBackPathFilename	= tParentPath + tBackFilename;
			
			if(_read){
							
				FileConnection t_back = (FileConnection) Connector.open(tBackPathFilename,Connector.READ_WRITE);
				try{
					if(t_back.exists()){
						FileConnection t_ini	= (FileConnection) Connector.open(_pathFilename,Connector.READ_WRITE);
						try{
							if(t_ini.exists()){
								t_ini.delete();
							}	
						}finally{
							t_ini.close();
							t_ini = null;
						}
						
						t_back.rename(tOrgFilename);
					}
				}finally{
					t_back.close();
					t_back = null;
				}				
				
			}else{
				
				FileConnection t_ini	= (FileConnection) Connector.open(_pathFilename,Connector.READ_WRITE);
				try{
					if(t_ini.exists()){
						t_ini.rename(tBackFilename);
					}
				}finally{
					t_ini.close();
					t_ini = null;
				}
				
				// needn't copy ,the normal WriteReadIni method will re-create the init.data file
				//
				//Copyfile(fsm_backInitFilename,fsm_initFilename);
			}
			
		}catch(Exception e){
			SetErrorString("write/read PreWriteReadIni file from SDCard error :" + e.getMessage() + e.getClass().getName());
		}
	}
	
	/**
	 * delete the back file ~xxx.xxx
	 * @param _backfile
	 */
	private void postWriteReadIni(String _pathFilename){
		
		try{
			int tSlashIdx = _pathFilename.lastIndexOf('/');
			
			String tParentPath			= _pathFilename.substring(0,tSlashIdx + 1);
			String tOrgFilename 		= _pathFilename.substring(tSlashIdx + 1);
			
			String tBackFilename		= "~" + tOrgFilename;
			String tBackPathFilename	= tParentPath + tBackFilename;
			
			// delete the back file ~xxx.data
			//
			FileConnection t_backFile = (FileConnection) Connector.open(tBackPathFilename,Connector.READ_WRITE);
			try{
				if(t_backFile.exists()){
					t_backFile.delete();
				}
			}finally{
				t_backFile.close();
				t_backFile = null;
			}
		}catch(Exception e){
			SetErrorString("PWRI", e);
		}
	}
	
	final static int		fsm_clientVersion = 41;
		
	static final String fsm_initFilename = uploadFileScreen.fsm_rootPath_back + "YuchBerry/Init.data";

	
	public synchronized void WriteReadIni(boolean _read){
		
		// process the ~Init.data file to restore the destroy original file
		// that writing when device is down  
		//
		// check the issue 85 
		// http://code.google.com/p/yuchberry/issues/detail?id=85&colspec=ID%20Type%20Status%20Priority%20Stars%20Summary
		//
		preWriteReadIni(_read,fsm_initFilename);
		
		try{
			
			FileConnection fc = (FileConnection) Connector.open(fsm_initFilename,Connector.READ_WRITE);
			try{
				if(_read){
					
			    	if(fc.exists()){
			    		InputStream t_readFile = fc.openInputStream();
			    		try{
			    			final int t_currVer = sendReceive.ReadInt(t_readFile);
				    		
				    		m_hostname		= sendReceive.ReadString(t_readFile);
				    		m_port			= sendReceive.ReadInt(t_readFile);
				    		m_userPassword	= sendReceive.ReadString(t_readFile);
				    		
				    		// read the APN validate 
				    		//
				    		final int t_apnNum = sendReceive.ReadInt(t_readFile);
				    		for(int i = 0 ;i < t_apnNum;i++){
				    			APNSelector t_sel = new APNSelector();
				    			t_sel.m_name 		= sendReceive.ReadString(t_readFile);
				    			t_sel.m_validateNum	= sendReceive.ReadInt(t_readFile);
				    			m_APNList.addElement(t_sel);
				    		}
				    		
				    		if(t_currVer >= 2){
				    			m_useSSL = (t_readFile.read() == 0)?false:true;
				    		}
				    		
				    		
				    		if(t_currVer >= 4){
				    			m_useWifi = (t_readFile.read() == 0)?false:true;
				    			m_appendString = sendReceive.ReadString(t_readFile);		    			
				    		}
				    						    		
				    		if(t_currVer >= 5){
				    			m_autoRun = (t_readFile.read() == 0)?false:true;		    			
				    		}
				    						    		
				    		if(t_currVer >= 6){
				    			m_uploadByte = sendReceive.ReadLong(t_readFile);
				    			m_downloadByte = sendReceive.ReadLong(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 7){
				    			m_pulseIntervalIndex = sendReceive.ReadInt(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 8){
				    			m_fulldayPrompt = sendReceive.ReadBoolean(t_readFile);
				    			m_startPromptHour = sendReceive.ReadInt(t_readFile);
			    				m_endPromptHour = sendReceive.ReadInt(t_readFile);		    			
				    		}	
				    		
				    		if(t_currVer >= 8){
				    			m_useLocationInfo = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 10){
				    			m_useMDS = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 11){
				    			m_sendMailNum = sendReceive.ReadInt(t_readFile);
				    			m_recvMailNum = sendReceive.ReadInt(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 12){
				    			m_discardOrgText = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 13){
				    			m_delRemoteMail = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 14){
				    			m_recvMsgTextLengthIndex = sendReceive.ReadInt(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 15){
				    			m_copyMailToSentFolder = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 16){
				    			m_passwordKey = sendReceive.ReadString(t_readFile); 
				    		}
				    		
				    		if(t_currVer >= 17){
				    			m_enableWeiboModule = sendReceive.ReadBoolean(t_readFile);
				    			m_updateOwnListWhenFw = sendReceive.ReadBoolean(t_readFile);
				    			m_updateOwnListWhenRe = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 18){
				    			sm_commentFirst = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 19){
				    			m_publicForward		= sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 20){
				    			m_maxWeiboNumIndex	= sendReceive.ReadInt(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 21){
				    			m_receivedWeiboNum	= sendReceive.ReadInt(t_readFile);
				    			m_sentWeiboNum		= sendReceive.ReadInt(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 22){
				    			sm_displayHeadImage = sendReceive.ReadBoolean(t_readFile);
				    			sm_simpleMode = sendReceive.ReadBoolean(t_readFile);
				    			m_dontDownloadWeiboHeadImage = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 23){
				    			m_hideHeader	= sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 24){
				    			m_connectDisconnectPrompt = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 25){
				    			sm_showAllInList = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 26){
				    			m_hasPromptToCheckImg	= sendReceive.ReadBoolean(t_readFile);
				    			m_checkImgIndex			= t_readFile.read();
				    		}
				    		
				    		if(t_currVer >= 27){
				    			m_spaceDownWeiboShortcutKey = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 28){
				    			m_mailUseLocation  = sendReceive.ReadBoolean(t_readFile);
				    			m_weiboUseLocation = sendReceive.ReadBoolean(t_readFile);
				    			m_refreshWeiboIntervalIndex = sendReceive.ReadInt(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 29){
				    			m_weiboUploadImageSizeIndex = t_readFile.read();
				    			m_defaultSendMailAccountIndex = sendReceive.ReadInt(t_readFile);
				    			sendReceive.ReadStringVector(t_readFile, m_sendMailAccountList);				    			
				    		}
				    		
				    		if(t_currVer >= 30){
				    			m_enableIMModule = sendReceive.ReadBoolean(t_readFile);
				    			m_enableChatChecked = sendReceive.ReadBoolean(t_readFile);
				    			m_enableChatState = sendReceive.ReadBoolean(t_readFile);
				    			m_hideUnvailiableRoster	= sendReceive.ReadBoolean(t_readFile);
				    			
				    			m_imCurrUseStatusIndex = sendReceive.ReadInt(t_readFile);
				    			
				    			sm_imStatusList.removeAllElements();
				    			
				    			int t_num = sendReceive.ReadInt(t_readFile);
				    			for(int i = 0 ;i < t_num;i++){
				    				IMStatus t_status = new IMStatus();
				    				t_status.Import(t_readFile);
				    				
				    				sm_imStatusList.addElement(t_status);
				    			}				    			
				    			if(m_imCurrUseStatusIndex < 0 || m_imCurrUseStatusIndex >= sm_imStatusList.size()){
				    				m_imCurrUseStatusIndex = 0;
				    			}
				    		}
				    		
				    		if(t_currVer >= 31){
				    			m_imChatMsgHistory = sendReceive.ReadInt(t_readFile);
				    			m_imChatScreenReceiveReturn = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 32){
				    			sm_imDisplayTime	= sendReceive.ReadBoolean(t_readFile);
				    			m_imReturnSend		= sendReceive.ReadBoolean(t_readFile);
				    			m_imPopupPrompt		= sendReceive.ReadBoolean(t_readFile);
				    			m_autoLoadNewTimelineWeibo = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 33){
				    			m_imChatScreenReverse	= sendReceive.ReadBoolean(t_readFile);
				    			m_imVoiceImmMode		= sendReceive.ReadBoolean(t_readFile);			    			
				    		}
				    		
				    		if(t_currVer >= 34){
				    			m_imSendImageQuality	= sendReceive.ReadInt(t_readFile);
				    			sm_standardUI			= sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 35){
				    			m_closeMailSendModule = sendReceive.ReadBoolean(t_readFile);				    			
				    		}
				    		
				    		if(t_currVer >= 36){
				    			m_imStoreImageVoice = sendReceive.ReadBoolean(t_readFile);
				    		}				    		
				    		
				    		if(t_currVer >= 37){
				    			m_weiboDontReadHistroy = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 38){
				    			m_imRenotifyPrompt = sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 39){
				    			
				    			m_weiboAccountList.removeAllElements();
				    			
				    			int t_accountNum = sendReceive.ReadInt(t_readFile);
				    			for(int i = 0;i < t_accountNum;i++){
				    				WeiboAccount acc = new WeiboAccount();
				    				acc.Input(t_readFile);
				    				
				    				m_weiboAccountList.addElement(acc);
				    			}
				    		}
				    		
				    		if(t_currVer >= 40){
				    			m_hideBackgroundIcon 		= sendReceive.ReadBoolean(t_readFile);
				    			m_imChatScreenShowHeadImg	= sendReceive.ReadBoolean(t_readFile);
				    			m_account					= sendReceive.ReadString(t_readFile);
				    			m_carrier					= sendReceive.ReadString(t_readFile);
				    		}
				    		
				    		if(t_currVer >= 41){
				    			m_markReadMailInSvr			= sendReceive.ReadBoolean(t_readFile);
				    			m_popupDlgWhenDisconnect	= sendReceive.ReadBoolean(t_readFile);
				    			m_popupDlgWhenComposeNew	= sendReceive.ReadBoolean(t_readFile);
				    			m_mailHtmlShow				= sendReceive.ReadBoolean(t_readFile);
				    			m_mailHtmlShowOnlyWIFI		= sendReceive.ReadBoolean(t_readFile);
				    		}
				    		
				    		
			    		}finally{
			    			t_readFile.close();
			    			t_readFile = null;
			    		}
			    	}
				}else{
					if(!fc.exists()){
						fc.create();
					}				
					
					OutputStream t_writeFile = fc.openOutputStream();
					try{
						sendReceive.WriteInt(t_writeFile,fsm_clientVersion);
						
						sendReceive.WriteString(t_writeFile, m_hostname);
						sendReceive.WriteInt(t_writeFile,m_port);
						sendReceive.WriteString(t_writeFile, m_userPassword);
						
						// write the APN name and validate number
						//
						sendReceive.WriteInt(t_writeFile,m_APNList.size());
						for(int i = 0 ;i < m_APNList.size();i++){
							APNSelector t_sel = (APNSelector)m_APNList.elementAt(i);
							sendReceive.WriteString(t_writeFile,t_sel.m_name);
							sendReceive.WriteInt(t_writeFile,t_sel.m_validateNum);
						}
						
						sendReceive.WriteBoolean(t_writeFile,m_useSSL);
						sendReceive.WriteBoolean(t_writeFile,m_useWifi);
						sendReceive.WriteString(t_writeFile,m_appendString);
						
						sendReceive.WriteBoolean(t_writeFile,m_autoRun);
						
						sendReceive.WriteLong(t_writeFile,m_uploadByte);
						sendReceive.WriteLong(t_writeFile, m_downloadByte);
						
						sendReceive.WriteInt(t_writeFile,m_pulseIntervalIndex);
						
						
						sendReceive.WriteBoolean(t_writeFile,m_fulldayPrompt);
						sendReceive.WriteInt(t_writeFile,m_startPromptHour);
						sendReceive.WriteInt(t_writeFile,m_endPromptHour);
						
						sendReceive.WriteBoolean(t_writeFile,m_useLocationInfo);
						sendReceive.WriteBoolean(t_writeFile,m_useMDS);
						
						sendReceive.WriteInt(t_writeFile,m_sendMailNum);
						sendReceive.WriteInt(t_writeFile,m_recvMailNum);
						
						sendReceive.WriteBoolean(t_writeFile,m_discardOrgText);
						sendReceive.WriteBoolean(t_writeFile,m_delRemoteMail);
						
						sendReceive.WriteInt(t_writeFile, m_recvMsgTextLengthIndex);
						
						sendReceive.WriteBoolean(t_writeFile,m_copyMailToSentFolder);
						
						sendReceive.WriteString(t_writeFile,m_passwordKey);
						
						sendReceive.WriteBoolean(t_writeFile,m_enableWeiboModule);
						sendReceive.WriteBoolean(t_writeFile,m_updateOwnListWhenFw);
						sendReceive.WriteBoolean(t_writeFile,m_updateOwnListWhenRe);
						sendReceive.WriteBoolean(t_writeFile,sm_commentFirst);
						sendReceive.WriteBoolean(t_writeFile,m_publicForward);
						
						sendReceive.WriteInt(t_writeFile,m_maxWeiboNumIndex);
						sendReceive.WriteInt(t_writeFile,m_receivedWeiboNum);
						sendReceive.WriteInt(t_writeFile,m_sentWeiboNum);
						
						sendReceive.WriteBoolean(t_writeFile,sm_displayHeadImage);
						sendReceive.WriteBoolean(t_writeFile,sm_simpleMode);
						sendReceive.WriteBoolean(t_writeFile,m_dontDownloadWeiboHeadImage);
						
						sendReceive.WriteBoolean(t_writeFile,m_hideHeader);
						
						sendReceive.WriteBoolean(t_writeFile,m_connectDisconnectPrompt);
						sendReceive.WriteBoolean(t_writeFile,sm_showAllInList);
						
						sendReceive.WriteBoolean(t_writeFile, m_hasPromptToCheckImg);
						t_writeFile.write(m_checkImgIndex);
						sendReceive.WriteBoolean(t_writeFile,m_spaceDownWeiboShortcutKey);
						
						sendReceive.WriteBoolean(t_writeFile,m_mailUseLocation);
						sendReceive.WriteBoolean(t_writeFile,m_weiboUseLocation);
						sendReceive.WriteInt(t_writeFile,m_refreshWeiboIntervalIndex);
						
						t_writeFile.write(m_weiboUploadImageSizeIndex);
						sendReceive.WriteInt(t_writeFile,m_defaultSendMailAccountIndex);
						sendReceive.WriteStringVector(t_writeFile, m_sendMailAccountList);
						
						sendReceive.WriteBoolean(t_writeFile,m_enableIMModule);
		    			sendReceive.WriteBoolean(t_writeFile,m_enableChatChecked);
		    			sendReceive.WriteBoolean(t_writeFile,m_enableChatState);
		    			sendReceive.WriteBoolean(t_writeFile,m_hideUnvailiableRoster);
		    			
		    			sendReceive.WriteInt(t_writeFile,m_imCurrUseStatusIndex);		    			
		    			sendReceive.WriteInt(t_writeFile,sm_imStatusList.size());
		    			for(int i = 0;i < sm_imStatusList.size();i++){
		    				IMStatus status = (IMStatus)sm_imStatusList.elementAt(i);
		    				status.Ouput(t_writeFile);
		    			}
		    			sendReceive.WriteInt(t_writeFile,m_imChatMsgHistory);
		    			sendReceive.WriteBoolean(t_writeFile,m_imChatScreenReceiveReturn);
		    			
		    			sendReceive.WriteBoolean(t_writeFile,sm_imDisplayTime);
		    			sendReceive.WriteBoolean(t_writeFile, m_imReturnSend);
		    			sendReceive.WriteBoolean(t_writeFile, m_imPopupPrompt);
		    			
		    			sendReceive.WriteBoolean(t_writeFile,m_autoLoadNewTimelineWeibo);
		    			
		    			sendReceive.WriteBoolean(t_writeFile,m_imChatScreenReverse);
		    			sendReceive.WriteBoolean(t_writeFile,m_imVoiceImmMode);
		    			
		    			sendReceive.WriteInt(t_writeFile,m_imSendImageQuality);
		    			sendReceive.WriteBoolean(t_writeFile,sm_standardUI);
		    			sendReceive.WriteBoolean(t_writeFile,m_closeMailSendModule);
		    			
		    			sendReceive.WriteBoolean(t_writeFile,m_imStoreImageVoice);
		    			sendReceive.WriteBoolean(t_writeFile,m_weiboDontReadHistroy);
		    			sendReceive.WriteBoolean(t_writeFile,m_imRenotifyPrompt);
		    			
		    			sendReceive.WriteInt(t_writeFile,m_weiboAccountList.size());
		    			for(int i = 0;i < m_weiboAccountList.size();i++){
		    				WeiboAccount acc = (WeiboAccount)m_weiboAccountList.elementAt(i);
		    				acc.Output(t_writeFile);
		    			}
		    			
		    			sendReceive.WriteBoolean(t_writeFile,m_hideBackgroundIcon);
		    			sendReceive.WriteBoolean(t_writeFile,m_imChatScreenShowHeadImg);
		    			sendReceive.WriteString(t_writeFile,m_account);
		    			sendReceive.WriteString(t_writeFile,m_carrier);
		    			sendReceive.WriteBoolean(t_writeFile, m_markReadMailInSvr);
		    			sendReceive.WriteBoolean(t_writeFile, m_popupDlgWhenDisconnect);
		    			sendReceive.WriteBoolean(t_writeFile, m_popupDlgWhenComposeNew);
		    			sendReceive.WriteBoolean(t_writeFile,m_mailHtmlShow);
		    			sendReceive.WriteBoolean(t_writeFile,m_mailHtmlShowOnlyWIFI);
		    									
						if(m_connectDeamon.m_connect != null){
							m_connectDeamon.m_connect.SetKeepliveInterval(GetPulseIntervalMinutes());
						}
						
					}finally{
						t_writeFile.close();
						t_writeFile = null;
					}
				}
			}finally{
				fc.close();
				fc = null;
			}
						
		}catch(Exception _e){
			SetErrorString("write/read config file error :" + _e.getMessage() + _e.getClass().getName());
		}
		
		postWriteReadIni(fsm_initFilename);
		
		if(m_locationProvider != null){
			if(m_useLocationInfo){
				
				if(m_setLocationListener == false){
					m_setLocationListener = true;
					
					m_locationProvider.setLocationListener(this, -1, 1, 1);
				}
				
			}else{
				
				if(m_setLocationListener == true){
					
					m_setLocationListener = false;
					m_locationProvider.reset();
					m_locationProvider.setLocationListener(null, -1, -1, -1);
				}				
			}
		}
	
	}
	
	//@{ LocationListener
	public void locationUpdated(LocationProvider provider, Location location){
	    // Respond to the updated location.
	    // If the application registered the location listener with an interval of
	    // 0, the location provider does not provide location updates.
		if(location.isValid()){
			m_gpsInfo.m_heading 	= location.getCourse();
			m_gpsInfo.m_longitude 	= location.getQualifiedCoordinates().getLongitude();
			m_gpsInfo.m_latitude 	= location.getQualifiedCoordinates().getLatitude();
			m_gpsInfo.m_altitude 	= location.getQualifiedCoordinates().getAltitude();
			m_gpsInfo.m_speed 		= location.getSpeed();
			
			if(m_settingScreen != null){
				m_settingScreen.RefreshLocation();
			}
		}
    }
   
	public void providerStateChanged(LocationProvider provider, int newState){
	   switch (newState) {
	     case LocationProvider.AVAILABLE :
	         // The location provider is available.
	    	 break;
	     case LocationProvider.OUT_OF_SERVICE :
	    	 // The location provider is permanently unavailable.
	    	 // Consider cancelling the location listener by calling
	    	 // provider.setLocationListener() with null as the listener.
	    	 break;
	     case LocationProvider.TEMPORARILY_UNAVAILABLE :
	    	 // The location provider is temporarily unavailable.
	        break;
	   	}
       
	}
		
	public synchronized void StoreUpDownloadByte(long _uploadByte,long _downloadByte,boolean _writeIni){
		m_uploadByte += _uploadByte;
		m_downloadByte += _downloadByte;	
				
		if(_writeIni){
			WriteReadIni(false);
		}
	}
	
	public synchronized void ClearUpDownloadByte(){

		m_uploadByte = m_downloadByte = 0;
				
		WriteReadIni(false);
	}
	
	
	public void Start(){
        
		ApplicationMenuItemRepository.getInstance().addMenuItem(ApplicationMenuItemRepository.MENUITEM_EMAIL_EDIT,m_addItem);
		ApplicationMenuItemRepository.getInstance().addMenuItem(ApplicationMenuItemRepository.MENUITEM_EMAIL_EDIT,m_delItem);
				
		WriteReadIni(false);
	}
	
	public void Exit(){
		
		try{
			if(m_connectDeamon.IsConnectState()){
				m_connectDeamon.Disconnect();
			}	
		}catch(Exception e){}		
		
		ApplicationMenuItemRepository.getInstance().removeMenuItem(ApplicationMenuItemRepository.MENUITEM_EMAIL_EDIT, m_addItem);
		ApplicationMenuItemRepository.getInstance().removeMenuItem(ApplicationMenuItemRepository.MENUITEM_EMAIL_EDIT,m_delItem);
		
		DisableWeiboModule();
		
		StopNotification();
		StopWeiboHomeNotification();
		StopWeiboNotification();
		StopDisconnectNotification();
		StopEmailFailedNotifaction();
		
		Indicator.unregisterIndicator();
		
		if(m_connectDeamon.m_connect != null){
			m_connectDeamon.m_connect.StoreUpDownloadByteImm(true);
		}
		
		if(m_locationProvider != null){
			if(m_useLocationInfo){
				m_locationProvider.reset();
				m_locationProvider.setLocationListener(null, -1, -1, -1);
			}
		}
		
		// store the weibo item list
		if(ReadWriteWeiboFile(false)){
			endFlurry();
			System.exit(0);
		}		
		
	}
	
	public boolean m_isChatScreen = false;
	public boolean m_isWeiboOrIMScreen = true;
	boolean m_weiboUpdateDlg = false;
	
	public void activate(){
		
		if(m_connectDeamon.IsConnectState()){
			
			if(m_enableWeiboModule){
				if(m_weiboTimeLineScreen == null){
					InitWeiboModule();
				}
			}
			
			if(m_enableIMModule){
				if(m_mainIMScreen == null){
					initIMModule();
				}
			}
			
			if(getScreenCount() == 0){   
				
				if(m_isWeiboOrIMScreen && m_weiboTimeLineScreen != null){
					m_isWeiboOrIMScreen = false;
					pushScreen(m_weiboTimeLineScreen);
					
					if(m_weiboUpdateDlg){
						m_weiboUpdateDlg = false;
						pushScreen(m_weiboTimeLineScreen.m_currUpdateDlg);
					}
					
				}else if(m_mainIMScreen != null){
					
					pushScreen(m_mainIMScreen);
					
					if(m_isChatScreen){
						m_isChatScreen = false;
						pushScreen(m_mainIMScreen.m_chatScreen);
					}
				}else{
					pushStateScreen();
				}
			}			
			
		}else{
			pushStateScreen();
		}	
		
		PopupLatestVersionDlg();
		
		if(m_hasNewWeibo){
			m_hasNewWeibo = false;
			
			if(m_connectDeamon.isDisconnectState()){
				HomeScreen.updateIcon(Bitmap.getBitmapResource("Main_offline.png"));
			}else{
				HomeScreen.updateIcon(Bitmap.getBitmapResource("Main.png"));
			}			
		}
	}
	
	public void deactivate(){
		
		if(m_enableWeiboModule || m_enableIMModule){
			
			if(m_stateScreen != null && getActiveScreen() == m_stateScreen){
				popScreen(m_stateScreen);
				m_stateScreen = null;
			}
			
			if(getActiveScreen() == PhizSelectedScreen.sm_phizScreen
				&& PhizSelectedScreen.sm_phizScreen != null){
						
				PhizSelectedScreen.sm_phizScreen.close();
				
			}else if(m_shareScreen != null
				&& getActiveScreen() == m_shareScreen){
				m_shareScreen.close();
			}
			
			if(m_weiboTimeLineScreen != null ){
				
				m_isWeiboOrIMScreen = true;
				
				if(m_weiboTimeLineScreen.m_pushUpdateDlg
				&& getActiveScreen() == m_weiboTimeLineScreen.m_currUpdateDlg){
					m_weiboUpdateDlg = true;
					m_weiboTimeLineScreen.m_currUpdateDlg.close();
				}
				
				if(m_weiboTimeLineScreen.m_optionScreen != null 
				&& getActiveScreen() == m_weiboTimeLineScreen.m_optionScreen){
					
					m_weiboTimeLineScreen.m_optionScreen.close();
					
				}else if(m_weiboTimeLineScreen.m_smsShareDlg != null 
					&& (getActiveScreen() == m_weiboTimeLineScreen.m_smsShareDlg 
						|| getActiveScreen() == m_weiboTimeLineScreen.m_smsShareDlg.m_sendingDlg)){
					
					m_weiboTimeLineScreen.m_smsShareDlg.close();
				}
				
				if(getActiveScreen() == m_weiboTimeLineScreen){
					popScreen(m_weiboTimeLineScreen);
				}
				
				m_weiboTimeLineScreen.autoLoadTimelineWeibo();
			}
			
			if(m_mainIMScreen != null){
				
				Screen t_activeScreen = getActiveScreen();
				
				if(t_activeScreen == m_mainIMScreen.m_chatScreen
					&& m_mainIMScreen.m_chatScreen != null){
					
					m_isWeiboOrIMScreen = false;
									
					popScreen(m_mainIMScreen.m_chatScreen);
					
					while(getScreenCount() != 0){
						// is NOT IM prompt dialog popup and MainChatScreen.close()
						// ( called mainApp.requestBackground ) 
						//
						m_isChatScreen = true;
						popScreen(getActiveScreen());
					}		
					
				}else if(t_activeScreen == m_mainIMScreen){
					
					m_isWeiboOrIMScreen = false;
					popScreen(m_mainIMScreen);
					
				}else{
										
					Screen[] t_screenList = 
					{
						m_mainIMScreen.m_statusAddScreen,
						m_mainIMScreen.m_optionScreen,
						m_mainIMScreen.m_addRosterDlg,
						m_mainIMScreen.m_searchStatus,
						m_mainIMScreen.m_checkRosterInfoScreen,
						m_mainIMScreen.m_aliasDlg,						
					};
					
					
					for(int i = 0;i < t_screenList.length;i++){
						if(t_activeScreen == t_screenList[i] && t_screenList[i] != null){
							m_isWeiboOrIMScreen = false;
							t_screenList[i].close();
							popScreen(m_mainIMScreen);
							break;
						}
					}
				}
			}
			
			
		}else{

			if(m_stateScreen != null && getActiveScreen() == m_stateScreen){
				popScreen(m_stateScreen);
				m_stateScreen = null;
			}
		}
	}
	
	public void pushStateScreen(){
		if(m_stateScreen == null){
			m_stateScreen = new stateScreen(this);	
		}
		
		if(getScreenCount() == 0){
			pushScreen(m_stateScreen);
		}		
	}
	
	public void popStateScreen(){
		if(m_stateScreen != null){
			popScreen(m_stateScreen);
			m_stateScreen = null;
		}	
	}
		
	public void TriggerNotification(){
		if(IsPromptTime()){
			NotificationsManager.triggerImmediateEvent(fsm_notifyID_email, 0, this, null);
		}		
	}
	public void StopNotification(){
		NotificationsManager.cancelImmediateEvent(fsm_notifyID_email, 0, this, null);
	}
	
	public void TriggerWeiboNotification(){
		if(IsPromptTime()){
			NotificationsManager.triggerImmediateEvent(fsm_notifyID_weibo, 0, this, null);
		}
		
		Indicator.notifyWeibo();
	}
	
	public void StopWeiboNotification(){
		NotificationsManager.cancelImmediateEvent(fsm_notifyID_weibo, 0, this, null);
		
		Indicator.disableNotifiyWeibo();
	}
	
	public void TriggerWeiboHomeNotification(){
		if(IsPromptTime()){
			NotificationsManager.triggerImmediateEvent(fsm_notifyID_weibo_home, 0, this, null);
		}		
	}
	
	public void StopWeiboHomeNotification(){
		NotificationsManager.cancelImmediateEvent(fsm_notifyID_weibo_home, 0, this, null);
	}
	
	public void TriggerDisconnectNotification(){
		if(IsPromptTime() && m_connectDisconnectPrompt){
			NotificationsManager.triggerImmediateEvent(fsm_notifyID_disconnect, 0, this, null);
		}
		
		if(m_popupDlgWhenDisconnect){
			DialogAlert(yblocalResource.CONNECT_NET_BROKEN_PROMPT);
		}
	}
	
	public void StopDisconnectNotification(){
		NotificationsManager.cancelImmediateEvent(fsm_notifyID_disconnect, 0, this, null);
		NotificationsManager.cancelImmediateEvent(fsm_notifyID_net_broken, 0, this, null);
	}
	
	public void TriggerIMNotification(){
		if(IsPromptTime()){
			NotificationsManager.triggerImmediateEvent(fsm_notifyID_im, 0, this, null);
		}
	}
	
	public void StopIMNotification(){
		NotificationsManager.cancelImmediateEvent(fsm_notifyID_im, 0, this, null);
		
		Indicator.disableNotifyIM();
	}
	
	public void TriggerEmailFailedNotifaction(){
		if(IsPromptTime()){
			NotificationsManager.triggerImmediateEvent(fsm_notifyID_email_failed, 0, this, null);
		}
	}
	
	public void StopEmailFailedNotifaction(){
		NotificationsManager.cancelImmediateEvent(fsm_notifyID_email_failed, 0, this, null);
	}
	
	public void TriggerNetBrokenNotifaction(){
		if(IsPromptTime()){
			NotificationsManager.triggerImmediateEvent(fsm_notifyID_net_broken, 0, this, null);
		}
		
		if(m_popupDlgWhenDisconnect){
			DialogAlert(yblocalResource.CONNECT_NET_BROKEN_PROMPT);
		}
	}
		
	public void PopupAboutScreen(){
		m_aboutScreen = new aboutScreen(this);
		pushScreen(m_aboutScreen);
	}
	
	public void PopupShareScreen(){
		try{
			m_shareScreen = new shareYBScreen(this);
			pushScreen(m_shareScreen);
		}catch(Exception e){
			DialogAlert("Read Address Error:" + e.getMessage());
		}
	}
	
	public void PopupSettingScreen(){
		m_settingScreen = new settingScreen(this);
		pushScreen(m_settingScreen);
	}
	
	public void PopupWeiboScreen(){
		
		if(m_weiboTimeLineScreen != null){
			
			if(getActiveScreen() == m_stateScreen && m_stateScreen != null){
				popStateScreen();
			}
			
			pushScreen(m_weiboTimeLineScreen);
		}		
	}
	
	public void PopupIMScreen(){
		
		if(m_enableIMModule){
			if(m_mainIMScreen == null){
				initIMModule();
			}
			
			if(getActiveScreen() == m_stateScreen && m_stateScreen != null){
				popStateScreen();
			}
			
			pushScreen(m_mainIMScreen);
		}		
	}
	
	public void PopupDownloadFileDlg(FetchAttachment _att){
		m_downloadDlg = new downloadDlg(this,_att);		
		UiApplication.getUiApplication().pushScreen(m_downloadDlg);		
	}
	
	public void refreshDownloadFileDlg(FetchAttachment _att){
		if(m_downloadDlg == null){
			
			PopupDownloadFileDlg(_att);
			
		}else{
			// found...
			if(m_downloadDlg.m_parent.getActiveScreen() != m_downloadDlg){
				m_downloadDlg.m_parent.pushScreen(m_downloadDlg);
			}
		}
		
		m_downloadDlg.RefreshProgress(_att);
	}
	
	public void SetModuleOnlineState(boolean _state){
		
		if(_state){
			if(m_hasNewWeibo){
				HomeScreen.updateIcon(Bitmap.getBitmapResource("Main_new.png"));
			}else{
				HomeScreen.updateIcon(Bitmap.getBitmapResource("Main.png"));
			}
		}else{
			if(m_hasNewWeibo){
				HomeScreen.updateIcon(Bitmap.getBitmapResource("Main_offline_new.png"));
			}else{
				HomeScreen.updateIcon(Bitmap.getBitmapResource("Main_offline.png"));
			}
		}		
		
		if(m_weiboTimeLineScreen != null){
			m_weiboTimeLineScreen.getHeader().invalidate();
		}
		
		if(m_mainIMScreen != null){
			m_mainIMScreen.getHeader().invalidate();
		}
	}
	
	public void SetAboutInfo(String _about){
		m_aboutString = _about;
		
		// prompt by the background thread
		//
		invokeLater(new Runnable(){
			public void run(){
				if(m_aboutScreen != null){
					m_aboutScreen.RefreshText();
				}
			}
		});		
	}
	
	public String	m_uploadFileFavorPath 				= null;
	
	public Object OpenAttachmentFileScreen(Object _org,final boolean _del){
		
		try{

			uploadFileScreen t_mailAttachSelectScreen = new uploadFileScreen(this,_del,m_connectDeamon);
			UiApplication.getUiApplication().pushScreen(t_mailAttachSelectScreen);
			
			return t_mailAttachSelectScreen;
			
		}catch(Exception _e){
			SetErrorString("att screen error:" + _e.getMessage());
		}
		
		return null;
	}
	
	//! message composed screen to add context menu to return change mailsender dialog  
	public Object openChangeMailSenderDlg(Object _org){
		
		try{
			
			ChangeMailSenderDlg t_dlg = new ChangeMailSenderDlg(recvMain.this);
			UiApplication.getUiApplication().pushScreen(t_dlg);
			return t_dlg;
			
		}catch(Exception e){
			SetErrorString("OCMSD",e);
		}
		
		return _org;
	}
	
	/**
	 * load the change mail sender dialog menu item or remove it
	 */
	private boolean m_loadChangeMailSenderMenu = false; 
	
	/**
	 * load the change mail sender dialog menu item or remove it
	 * @param _load		load or remove
	 */
	public void loadChangeMailSenderMenu(boolean _load){
		
		// clear the temporary default sender
		m_defaultSendMailAccountIndex_tmp = -1;
		
		if(m_loadChangeMailSenderMenu != _load){
						
			if(_load){
				if(m_sendMailAccountList.size() <= 1){ //unique account...
					return;
				}
				try{
					ApplicationMenuItemRepository.getInstance().addMenuItem(ApplicationMenuItemRepository.MENUITEM_EMAIL_EDIT,m_changeDefaultSenderItem);
				}catch(Exception e){}
			}else{
				try{
					ApplicationMenuItemRepository.getInstance().removeMenuItem(ApplicationMenuItemRepository.MENUITEM_EMAIL_EDIT,m_changeDefaultSenderItem);
				}catch(Exception e){}				
			}
			
			m_loadChangeMailSenderMenu = _load;
		}
	}
	
	public void PushViewFileScreen(final String _filename){
		
		UiApplication.getUiApplication().invokeLater(new Runnable(){
			
		    public void run(){
		    	
		    	if(CheckMediaNativeApps(_filename)){
		    		return;
		    	}
		    	
		    	try{
		    		if(uploadFileScreen.IsAudioFile(_filename)){
		    			UiApplication.getUiApplication().pushGlobalScreen(new audioViewScreen(_filename),0,UiEngine.GLOBAL_MODAL);
		    		}else if(uploadFileScreen.IsTxtFile(_filename)){
		    			UiApplication.getUiApplication().pushGlobalScreen(new textViewScreen(_filename),0,UiEngine.GLOBAL_MODAL);
		    		}else if(uploadFileScreen.IsMovieFile(_filename)){
		    			UiApplication.getUiApplication().pushGlobalScreen(new videoViewScreen(_filename),0,UiEngine.GLOBAL_MODAL);		    					    			
		    		}else if(uploadFileScreen.IsImageFile(_filename)){
		    			UiApplication.getUiApplication().pushGlobalScreen(new imageViewScreen(_filename),0,UiEngine.GLOBAL_MODAL);			
		    		}else {
		    			DialogAlert("yuchberry prompt:unknow format");		    					    			
		    		}
		    		
		    	}catch(Exception _e){
		    		DialogAlert(_e.getMessage());
		    	}		    	
			}
		});
	}
	
	public boolean CheckMediaNativeApps(String _filename){
		
//		try{
//			Invocation request = new Invocation(_filename);
//			Registry registry = Registry.getRegistry(UiApplication.getUiApplication().getClass().getName());
//			registry.invoke(request);
//			
//			return true;
//		}catch(Exception e){
//			SetErrorString("Invoke native apps failed: "+ e.getMessage());
//		}
		
		return false;		
	}
	
	public void UpdateMessageStatus(final Message m,final int _status){
		
		if(m == null){
			return;
		}
		
		if(m_messageApplication != null && m_messageApplication.isAlive()){
			
			m_messageApplication.invokeAndWait(new Runnable() {
				public void run(){
					m.setStatus(_status,0);
					m.updateUi();
					m_messageApplication.relayout();
				}
			});
		}else{
			invokeLater(new Runnable() {
				
				public void run() {
					m.setStatus(_status,0);
					m.updateUi();
				}
			});							
		}		
		
	}
	
	public void PopupDlgToOpenAttach(final connectDeamon.FetchAttachment _att){
				
		if(m_downloadDlg != null){
			m_downloadDlg.m_parent.invokeLater(new Runnable() {
				public void run() {
					m_downloadDlg.m_parent.popScreen(m_downloadDlg);
					m_downloadDlg = null;
				}
			});			
		}
		
		// prompt by the background thread
		//
				
		Dialog t_dlg = new Dialog(Dialog.D_OK_CANCEL,_att.m_realName + sm_local.getString(yblocalResource.DOWNLOAD_OVER_PROMPT),
	    							Dialog.OK,Bitmap.getPredefinedBitmap(Bitmap.QUESTION),Manager.VERTICAL_SCROLL);
		
		t_dlg.setDialogClosedListener(new DialogClosedListener(){
			
			public void dialogClosed(Dialog dialog, int choice) {
				
				switch (choice) {
					case Dialog.OK:
						recvMain t_mainApp = (recvMain)UiApplication.getUiApplication();
						t_mainApp.PushViewFileScreen(t_mainApp.GetAttachmentDir() + _att.m_realName);
						break;
					
					default:
						break;
				}
			}
		});
		
		t_dlg.setEscapeEnabled(true);
		synchronized (getEventLock()) {
			pushGlobalScreen(t_dlg,1, UiEngine.GLOBAL_QUEUE);
		}		
	}
		
	public void SetConnectState(int _state){
		m_connectState = _state;

		if(m_stateScreen != null){
			m_stateScreen.setConnectButState(m_connectState,this);
		}		
	}
	
	private Dialog m_alartDlg = null;
	
	public void DialogAlert(final String _msg){

		SetErrorString(_msg);
		
		if(m_alartDlg != null){
			return;
		}
		
		invokeLater(new Runnable() {
			public void run(){
				synchronized(getEventLock()){
					
					synchronized (recvMain.this) {
						m_alartDlg = new Dialog(Dialog.D_OK,_msg,
								Dialog.OK,Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION),Manager.VERTICAL_SCROLL){
							public void close(){
								super.close();
								synchronized (recvMain.this) {
									m_alartDlg = null;
								}
							}
						};
						
						m_alartDlg.setEscapeEnabled(true);			
						pushGlobalScreen(m_alartDlg,1, UiEngine.GLOBAL_QUEUE);
					}
					
				};
			}
		});				
    }
	
	public void DialogAlert(int _yblocalResource){
		DialogAlert(sm_local.getString(_yblocalResource));
	}
 
	public void SetUploadingDesc(final fetchMail _mail,final int _attachmentIdx,
								final int _uploadedSize,final int _totalSize){
						
		boolean t_found = false;
		for(int i = 0;i < m_uploadingDesc.size();i++){
			UploadingDesc t_desc = (UploadingDesc)m_uploadingDesc.elementAt(i);
			if(t_desc.m_mail == _mail){
				
				t_found = true;
				
				if(_attachmentIdx == -2){					
					m_uploadingDesc.removeElement(t_desc);
					
				}else{
					t_desc.m_attachmentIdx	= _attachmentIdx;
					t_desc.m_totalSize		= _totalSize;
					t_desc.m_uploadedSize	= _uploadedSize;
					
				}
				break;
			}
		}
		
		if(_attachmentIdx != -1 && !t_found){
			UploadingDesc t_desc = new UploadingDesc();
			
			t_desc.m_mail 			= _mail;
			t_desc.m_totalSize 		= _totalSize;
			t_desc.m_uploadedSize	= _uploadedSize;
			
			m_uploadingDesc.addElement(t_desc);
		}
		
		invokeLater(new Runnable() {
			public void run() {
				if(m_stateScreen != null){
					m_stateScreen.RefreshUploadState(m_uploadingDesc);
				}
			}
		});
		
	}
	
	public final Vector GetUploadingDesc(){
		return m_uploadingDesc;
	}

	public final int GetConnectState(){
		return m_connectState;
	}
	
	public void LogOut(String _log){
		try{
			if(m_logfc == null){
				m_logfc = (FileConnection) Connector.open(uploadFileScreen.fsm_rootPath_back + "YuchBerry/log.txt",Connector.READ_WRITE);
				if(m_logfc.exists()){
					m_logfc.delete();
				}
				
				m_logfc.create();
				m_logfcOutput = m_logfc.openOutputStream();
			}
			
			m_logfcOutput.write(_log.getBytes());
			m_logfcOutput.write(("\n").getBytes());
			m_logfcOutput.flush();
			
		}catch(Exception e){
			SetErrorString("LogOut Error:"+e.getMessage() + e.getClass().getName());
		}
		
	}
	
	public synchronized void SetErrorString(final String _error){
		m_errorString.addElement(new ErrorInfo(_error));
		if(m_errorString.size() > 100){
			m_errorString.removeElementAt(0);
		}
		
		if(m_debugInfoScreen != null){
			m_debugInfoScreen.RefreshText();
		}			
	}
	
	public void SetErrorString(String _label,Exception _e){
		SetErrorString(_label + " " + _e.getMessage() + " " + _e.getClass().getName());
	}
	
	public synchronized String GetAllErrorString(){
		if(!m_errorString.isEmpty()){

			SimpleDateFormat t_format = new SimpleDateFormat("HH:mm:ss");
			
			ErrorInfo t_info = (ErrorInfo)m_errorString.elementAt(0);
			
			StringBuffer t_text = new StringBuffer();
			
			for(int i = m_errorString.size() - 1;i >= 0;i--){				
				t_info = (ErrorInfo)m_errorString.elementAt(i);
				t_text.append(t_format.format(t_info.m_time)).append(":").append(t_info.m_info).append("\n");
			}
			
			return t_text.toString();
		}
		
		return "";
	}
	public void clearDebugMenu(){
		m_errorString.removeAllElements();
		
		if(m_debugInfoScreen != null){
			m_debugInfoScreen.RefreshText();
		}
	}
	public final Vector GetErrorString(){
		return m_errorString;
	}
		
	static public void openURL(String _url){
		BrowserSession browserSession = Browser.getDefaultSession();
		browserSession.displayPage(_url);
	}
	
	static public String GetByteStr(long _byte){
		if(_byte < 1000){
			return "" + _byte + "B";
		}else if(_byte >= 1000 && _byte < 1000000){
			return "" + (_byte / 1000) + "." + (_byte % 1000 / 100)+ "KB";
		}else{
			return "" + (_byte / (1000000)) + "." + ((_byte / 1000) % 1000 / 100) + "MB";
		}
	}
	
	static public int GetClientLanguage(){
		int t_code = Locale.getDefaultForSystem().getCode();
		
		switch(t_code){
			case Locale.LOCALE_zh:
			case Locale.LOCALE_zh_CN:
				t_code = 0;
				break;
			case Locale.LOCALE_zh_HK:
				t_code = 1;
				break;
			default:
				t_code = 2;
				break;
		}
		
		return t_code;
	}
	
	public static String md5(String _org){
		
		byte[] bytes = null;
		try{
			bytes = _org.getBytes("UTF-8");
		}catch(Exception e){
			bytes = _org.getBytes();
		}
		
		MD5Digest digest = new MD5Digest();
		
		digest.update(bytes, 0, bytes.length);

		byte[] md5 = new byte[digest.getDigestLength()];
		digest.getDigest(md5, 0, true);
		
		return convertToHex(md5);
		
	}
	
	public static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
	
	public static String sprintf(int _expid,String[] _arg){
		return sprintf(sm_local.getString(_expid),_arg);
	}

	public static String sprintf(String _exp,String[] _arg){
		
		if(_arg == null){
			return _exp;
		}
		
		int t_idx 		= 0;
		int t_argIdx	= 0;
		
		while(t_argIdx < _arg.length){
			
			t_idx = _exp.indexOf("%s");
			if(t_idx == -1){
				break;
			}
			
			_exp = _exp.substring(0,t_idx) + _arg[t_argIdx++] + _exp.substring(t_idx + 2);
		}
		
		return _exp;
	}
	
	/**
	 * whether this string is valid Email  
	 * @param _str
	 * @return true if this string is email otherwise false
	 */
	public static boolean isValidateEmail(String _str){
		int t_at = _str.indexOf("@");
		if(t_at == -1){
			return false;
		}
		
		if(t_at == 0 || (_str.length() - 1) - t_at < 3){
			return false;
		}
		
		String t_addr = _str.substring(t_at + 1);
		
		int t_otherAt = t_addr.indexOf("@");
		if(t_otherAt != -1){
			return false;
		}
		
		int t_dot = t_addr.indexOf(".");
		if(t_dot == -1){
			return false;
		}
		
		if(t_dot == t_addr.length() - 1 ){
			return false;
		}
		
		return true;
	}
	
	/**
	 * is validate user pass 
	 * @param _str
	 * @return
	 */
	public static boolean isValidateUserPass(String _str){
		if(_str.length() < 6){
			return false;
		}
		
		
		for(int i = 0 ;i < _str.length();i++){
			char a = _str.charAt(i);
			if(!Character.isDigit(a) && !Character.isLowerCase(a) && !Character.isUpperCase(a)){
				return false;
			}
		}
		
		return true;
	}
	
//	static ImageManipulator		sm_manipulator = new ImageManipulator(null);
	public static Bitmap scaleImage(EncodedImage _image,int _width,int _height){
		
//		synchronized (sm_manipulator) {
//						
//			Bitmap t_bitmap = new Bitmap(_width,_height);
//			
//			sm_manipulator.setBitmap(_image.getBitmap());
//			sm_manipulator.scaleInto(t_bitmap,0,ImageManipulator.SCALE_TO_FILL);
//			
//			return t_bitmap;
//		}
		
		int t_origWidth = _image.getWidth();
		int t_origHeight = _image.getHeight();
		
		int scaleX = Fixed32.div(Fixed32.toFP(t_origWidth), Fixed32.toFP(_width));
		int scaleY = Fixed32.div(Fixed32.toFP(t_origHeight), Fixed32.toFP(_height));
		
		return _image.scaleImage32(scaleX, scaleY).getBitmap();
	}
	
	/**
	 * get the HTTP request which is opened by Connector.open() append string 
	 * @return
	 */
	public static String getHTTPAppendString(){
		String t_append = ";deviceside=true";
		
//		if( WLANInfo.getAPInfo() != null){
//			t_append += ";interface=wifi";
//		}else{
			
			String apn = recvMain.findNetworkAPN();
			if(apn != null){
				t_append += ";apn=" + apn;
			}
//		}
		
		return t_append;
	}
	
	/**
	 * replace the old String in replace one with newStr
	 * @param replaceStr
	 * @param oldStr
	 * @param newStr
	 * @return
	 */
	public static String repleaceStr(String replaceStr,String oldStr,String newStr){
		
		int idx = replaceStr.indexOf(oldStr);
		while(idx != -1){
			replaceStr = replaceStr.substring(0,idx) + newStr + replaceStr.substring(idx + oldStr.length()); 
			idx = replaceStr.indexOf(oldStr);
		}
		
		return replaceStr;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	///// weibo module
	///////////////////////////////////////////////////////////////////////////////////////////
	// weibo module
	public boolean			m_enableWeiboModule			= false;
	public boolean			m_updateOwnListWhenFw		= true;
	public boolean			m_updateOwnListWhenRe		= false;
	public boolean			m_dontDownloadWeiboHeadImage= false;
	public boolean			m_spaceDownWeiboShortcutKey	= true;
	
	public static	boolean		sm_commentFirst		= false;
	public static	boolean		sm_displayHeadImage	= true;
	public static boolean			sm_simpleMode		= false;
	public static boolean			sm_showAllInList	= false;
	public static boolean			sm_standardUI		= true;
	
				
	public weiboTimeLineScreen	m_weiboTimeLineScreen = null;
	public boolean				m_publicForward		= false;
	private Vector				m_receivedWeiboList	= new Vector();
	
	private int				m_receivedHomeWeiboNum = 0;
	private int				m_receivedAtMeWeiboNum = 0;
	private int				m_receivedCommentWeiboNum = 0;
	private int				m_receivedDirectMsgWeiboNum = 0;
	
	public static final String[]	fsm_maxWeiboNumList = {"64","128","256","512","1024"};
	public static final int[]	fsm_maxWeiboNum		= {64,128,256,512,1024};
	public int					m_maxWeiboNumIndex = 0;
	
	public int					m_receivedWeiboNum = 0;
	public int					m_sentWeiboNum = 0;
	public boolean				m_hideHeader = false;
	public boolean				m_hasNewWeibo = false;
	
	public boolean				m_hasPromptToCheckImg = true;
	public int					m_checkImgIndex = 1;

	public boolean				m_weiboUseLocation = false;
	public boolean				m_autoLoadNewTimelineWeibo = false;
	
	public boolean				m_weiboDontReadHistroy = false;
	
	public boolean 			mEnableWeibo2SMS = true;
	
	public static final String[]	fsm_refreshWeiboIntervalList = {"0","10","20","30","40","60","120","360","720","1440"};
	public static final int[]		fsm_refreshWeiboInterval		= {0,10,20,30,40,60,120,360,720,1440};
	public int						m_refreshWeiboIntervalIndex = 0;
	
	public static final String[]	fsm_weiboUploadImageSizeList = {"800×600","1280×800",sm_local.getString(yblocalResource.WEIBO_IMAGE_ORIGINAL_SIZE)};
	public static final XYPoint[]	fsm_weiboUploadImageSize_size		= 
	{
		new XYPoint(800,600),
		new XYPoint(1280,800),
		null,
	};
	
	public int						m_weiboUploadImageSizeIndex = 0;
	public Vector					m_weiboAccountList		= new Vector();
		
	/**
	 * get the automatic refresh Weibo interval time 
	 * @return
	 */		
	public int getRefreshWeiboInterval(){
		if(m_refreshWeiboIntervalIndex < fsm_refreshWeiboInterval.length){
			return fsm_refreshWeiboInterval[m_refreshWeiboIntervalIndex];
		}
		
		return 0;
	}
	
	boolean m_receiveWeiboListChanged = false;
	
	ApplicationMenuItem m_updateWeiboItem = null;
	
	public XYPoint getWeiboUploadSize(){
		
		if(m_weiboUploadImageSizeIndex >= 0 && m_weiboUploadImageSizeIndex < fsm_weiboUploadImageSize_size.length){
			return fsm_weiboUploadImageSize_size[m_weiboUploadImageSizeIndex];
		}else{
			m_weiboUploadImageSizeIndex = 0;
		}
		
		return null;
	}
	
	public static ImageSets			sm_weiboUIImage = null;
	public Vector						m_phizImageList = new Vector();
	public static Vector				sm_phizImageList = null;		
	
	public ObjectAllocator				m_weiboAllocator = new ObjectAllocator("com.yuchting.yuchberry.client.weibo.fetchWeibo");
	public ImageSets					m_weiboUIImage = null;
	
	public void loadImageSets(){
		
		if(sm_weiboUIImage == null){

			try{
				sm_weiboUIImage = new ImageSets("/weibo_full_image.imageset");
			}catch(Exception e){
				DialogAlertAndExit("weibo UI load Error:"+ e.getMessage() + e.getClass().getName());
			}
			
			Vector t_imageList = sm_weiboUIImage.getImageList();
			for(int i = 0;i < t_imageList.size();i++){
			    ImageUnit t_unit = (ImageUnit)t_imageList.elementAt(i);
			    
			    if(t_unit.getName().charAt(0) == '['){
			    	m_phizImageList.addElement(new Phiz(t_unit,sm_weiboUIImage));
			    }
			}
			
			sm_phizImageList = m_phizImageList;
			
			m_weiboUIImage = sm_weiboUIImage;
		}
	}
	
	public void InitWeiboModule(){
		
		if(m_enableWeiboModule){
			
			loadImageSets();
			
			if(m_weiboTimeLineScreen == null){
								
				m_weiboTimeLineScreen = new weiboTimeLineScreen(this);
				
				m_updateWeiboItem = new ApplicationMenuItem(30) {
					
					public String toString() {
						return recvMain.sm_local.getString(yblocalResource.WEIBO_UPDATE_DLG);
					}
					
					public Object run(Object context) {
						m_weiboTimeLineScreen.m_updateItem.run();
						return m_weiboTimeLineScreen.m_currUpdateDlg;
					}
				};
				
				ApplicationMenuItemRepository.getInstance()
					.addMenuItem(ApplicationMenuItemRepository.MENUITEM_MESSAGE_LIST,m_updateWeiboItem);
			}
			
			if(!m_weiboDontReadHistroy){
				ReadWriteWeiboFile(true);
			}
			
			
			m_weiboTimeLineScreen.ClearWeibo();
			
			m_receivedHomeWeiboNum = 0;
			m_receivedAtMeWeiboNum = 0;
			m_receivedCommentWeiboNum = 0;
			m_receivedDirectMsgWeiboNum = 0;
					
			if(!m_receivedWeiboList.isEmpty()){
				
				synchronized (m_receivedWeiboList) {
					for(int i = 0 ;i < m_receivedWeiboList.size();i++){
						fetchWeibo weibo = (fetchWeibo)m_receivedWeiboList.elementAt(i);
						switch(weibo.GetWeiboClass()){
						case fetchWeibo.TIMELINE_CLASS:
							m_receivedHomeWeiboNum++;
							break;
						case fetchWeibo.AT_ME_CLASS:
							m_receivedAtMeWeiboNum++;
							break;
						case fetchWeibo.COMMENT_ME_CLASS:
							m_receivedCommentWeiboNum++;
							break;
						case fetchWeibo.DIRECT_MESSAGE_CLASS:
							m_receivedDirectMsgWeiboNum++;
							break;
						}
					}
					
					for(int i = 0;i < m_receivedWeiboList.size();i++){
						fetchWeibo t_weibo = (fetchWeibo)m_receivedWeiboList.elementAt(i);
						m_weiboTimeLineScreen.AddWeibo(t_weibo,true);
					}
				}
			}
			
		}
	}
	
	public void DisableWeiboModule(){
		if(m_updateWeiboItem != null){
			ApplicationMenuItemRepository.getInstance()
				.removeMenuItem(ApplicationMenuItemRepository.MENUITEM_MESSAGE_LIST,m_updateWeiboItem);
			m_updateWeiboItem = null;
			m_weiboTimeLineScreen = null;
			
			StopWeiboHomeNotification();
			StopWeiboNotification();
		}
	}
	
	long m_weiboRefereshAccountTime = 0;
	public void sendRefreshWeiboAccountList(){
		long t_currTime = System.currentTimeMillis();
		if(Math.abs(t_currTime - m_weiboRefereshAccountTime) < 2 * 60000){
			return;
		}
		
		m_weiboRefereshAccountTime = t_currTime;
		
		try{
			m_connectDeamon.addSendingData(msg_head.msgWeiboAccountList, new byte[]{msg_head.msgWeiboAccountList}, true);
		}catch(Exception e){
			SetErrorString("SRWAL", e);
		}
		
	}
		
	public void PrepareWeiboItem(fetchWeibo _weibo){
		
		if(!isForeground()){
			m_hasNewWeibo = true;
			
			if(m_connectDeamon.isDisconnectState()){
				HomeScreen.updateIcon(Bitmap.getBitmapResource("Main_offline_new.png"));
			}else{
				HomeScreen.updateIcon(Bitmap.getBitmapResource("Main_new.png"));
			}
		}
		
		synchronized (this) {
			m_receiveWeiboListChanged = true;
		}
		
		m_receivedWeiboNum++;
		
		int t_checkWeiboClassNum = 0;
		
		switch(_weibo.GetWeiboClass()){
		case fetchWeibo.TIMELINE_CLASS:
			t_checkWeiboClassNum = ++m_receivedHomeWeiboNum;
			break;
		case fetchWeibo.AT_ME_CLASS:
			t_checkWeiboClassNum = ++m_receivedAtMeWeiboNum;
			break;
		case fetchWeibo.COMMENT_ME_CLASS:
			t_checkWeiboClassNum = ++m_receivedCommentWeiboNum;
			break;
		case fetchWeibo.DIRECT_MESSAGE_CLASS:
			t_checkWeiboClassNum = ++m_receivedDirectMsgWeiboNum;
			break;
		}
		
		synchronized (m_receivedWeiboList) {
			
			for(int i = m_receivedWeiboList.size() - 1 ;i >= 0 ;i--){
				fetchWeibo weibo = (fetchWeibo)m_receivedWeiboList.elementAt(i);
				if(weibo.equals(_weibo)){
					return;
				}
			}			
						
			if(t_checkWeiboClassNum >= getMaxWeiboNum()){
				
				for(int i = 0; i < m_receivedWeiboList.size();i++){
					
					fetchWeibo t_delWeibo = (fetchWeibo)m_receivedWeiboList.elementAt(i);
					
					if(t_delWeibo.GetWeiboClass() == _weibo.GetWeiboClass()){
						
						m_weiboAllocator.release(t_delWeibo);
						m_receivedWeiboList.removeElementAt(i);
						
						m_weiboTimeLineScreen.DelWeibo(t_delWeibo);
												
						break;
					}
				}
				
				switch(_weibo.GetWeiboClass()){
				case fetchWeibo.TIMELINE_CLASS:
					--m_receivedHomeWeiboNum;
					break;
				case fetchWeibo.AT_ME_CLASS:
					--m_receivedAtMeWeiboNum;
					break;
				case fetchWeibo.COMMENT_ME_CLASS:
					--m_receivedCommentWeiboNum;
					break;
				case fetchWeibo.DIRECT_MESSAGE_CLASS:
					--m_receivedDirectMsgWeiboNum;
					break;
				}
			}
			
			m_receivedWeiboList.addElement(_weibo);
		}
		
		m_weiboTimeLineScreen.AddWeibo(_weibo,false);
	}
	
	public void ChangeHeadImageHash(boolean _isWeiboOrIM,String _userId,int _weiboStyle,int _headImageHash){
		
		if(_isWeiboOrIM){
			synchronized(m_receivedWeiboList) {

				for(int i = 0 ;i < m_receivedWeiboList.size();i++){
					fetchWeibo weibo = (fetchWeibo)m_receivedWeiboList.elementAt(i);
					
					if(weibo.GetWeiboStyle() == _weiboStyle 
					&& weibo.GetHeadImageId().equals(_userId) ){
						
						weibo.SetUserHeadImageHashCode(_headImageHash);
					}
				}
			}
		}else{
			if(m_mainIMScreen != null){
				m_mainIMScreen.changeHeadImageHash(_userId,_weiboStyle,_headImageHash);
			}
		}
		
	}
	
	static final String fsm_weiboDataName = uploadFileScreen.fsm_rootPath_back + "YuchBerry/weibo.data";
		
	private synchronized boolean ReadWriteWeiboFile(final boolean _read){
		
		if(!m_receiveWeiboListChanged && !_read){
			return true;
		}
		
		m_receiveWeiboListChanged = false;		
					
		if(_read){
			
			readWriteWeiboFile_impl(_read);
			return true;
			
		}else{
			
			Dialog t_waitDlg = new Dialog(sm_local.getString(yblocalResource.WAITING_FOR_STORE_DATA),new Object[0],new int[0],0,null);
			t_waitDlg.show();
			
			invokeLater(new Runnable() {
				
				public void run() {
					readWriteWeiboFile_impl(_read);
					
					endFlurry();
					System.exit(0);
				}
			},100,false);
			
			return false;
		}
	}
	
	/**
	 * read or write weibo history list to a file sync
	 * @param _read
	 */
	private synchronized void readWriteWeiboFile_impl(final boolean _read){
		
		preWriteReadIni(_read, fsm_weiboDataName);		
		
		try{
			FileConnection t_fc = (FileConnection)Connector.open(fsm_weiboDataName);
			try{			
				if(_read){
					synchronized (m_receivedWeiboList) {
						m_receivedWeiboList.removeAllElements();
						
						if(t_fc.exists()){
							
							InputStream t_readIn = t_fc.openInputStream();
							try{
								
								int t_num = sendReceive.ReadInt(t_readIn);
								for(int i = 0 ;i < t_num;i++){
									fetchWeibo t_weibo = new fetchWeibo();
									t_weibo.InputWeibo(t_readIn);
									
									m_receivedWeiboList.addElement(t_weibo);
								}
								
							}finally{
								t_readIn.close();
								t_readIn = null;
							}
						}
					}
					
					
				}else{
					
					if(!t_fc.exists()){
						t_fc.create();
					}
					
					OutputStream t_fileos = t_fc.openOutputStream();
					try{

						ByteArrayOutputStream tmpos = new ByteArrayOutputStream();
												
						synchronized (m_receivedWeiboList) {
							sendReceive.WriteInt(tmpos,m_receivedWeiboList.size());
							
							for(int i = 0 ;i < m_receivedWeiboList.size();i++){
								fetchWeibo t_weibo = (fetchWeibo)m_receivedWeiboList.elementAt(i);
								t_weibo.OutputWeibo(tmpos);
							}
						}
						
						t_fileos.write(tmpos.toByteArray());
						t_fileos.flush();
						
					}finally{
						t_fileos.close();
						t_fileos = null;
					}
				}
				
			}finally{
				t_fc.close();
				t_fc = null;
			}
		}catch(Exception e){
			SetErrorString("RWWF:"+e.getMessage()+e.getClass().getName());
		}
		
		postWriteReadIni(fsm_weiboDataName);
	}
		
	///////////////////////////////////////////////////////////////////////////////////////////
	///// im module
	///////////////////////////////////////////////////////////////////////////////////////////
	public boolean				m_enableIMModule 		= false;
	public boolean				m_enableChatChecked 	= true;
	public boolean				m_enableChatState		= true;
	public boolean				m_hideUnvailiableRoster = true;
	
	public boolean 			m_imChatScreenReceiveReturn = false;
	
	public static boolean		sm_imDisplayTime		= true;
	public boolean				m_imChatScreenReverse	= false;
	public boolean				m_imVoiceImmMode		= false;
	
	public boolean				m_imReturnSend	= false;
	
	public boolean				m_imChatScreenShowHeadImg = false;
	
	/**
	 * IM option to popup a dialog prompt 
	 */
	public boolean				m_imPopupPrompt	= true;
	
	/**
	 * any chat message re-notify when backlight is disable 
	 */
	public boolean				m_imRenotifyPrompt = false;
	
	public boolean				m_imStoreImageVoice		= false;
		
	public int					m_imCurrUseStatusIndex	= 0;
	public static Vector		sm_imStatusList			= new Vector();
	static{
		sm_imStatusList.addElement(new IMStatus(fetchChatRoster.PRESENCE_AVAIL,sm_local.getString(yblocalResource.IM_STATUS_DEFAULT_AVAIL)));
		sm_imStatusList.addElement(new IMStatus(fetchChatRoster.PRESENCE_AWAY,sm_local.getString(yblocalResource.IM_STATUS_DEFAULT_AWAY)));
		sm_imStatusList.addElement(new IMStatus(fetchChatRoster.PRESENCE_BUSY,sm_local.getString(yblocalResource.IM_STATUS_DEFAULT_BUSY)));
	}
	
	public static final String[]	fsm_imChatMsgHistoryList 	= {"32","45","64","128","256"};
	public static final int[]		fsm_imChatMsgHistory		= {32,45,64,128,256};
	public int						m_imChatMsgHistory 			= 0;
	
	public static final String[]	fsm_imUploadImageSizeList = {"320×240","480×360","640×480"};
	public static final XYPoint[]	fsm_imUploadImageSize_size		= 
	{
		new XYPoint(320,240),
		new XYPoint(480,360),
		new XYPoint(640,480),
		null,
	};
	public int					m_imSendImageQuality		= 0;
	MainIMScreen				m_mainIMScreen = null;
	
	public void initIMModule(){
		
		if(m_enableIMModule){
			loadImageSets();
			
			if(m_mainIMScreen == null){
				m_mainIMScreen = new MainIMScreen(this);
			}			
		}
	}
	
	public XYPoint getIMSendImageQuality(){

		if(m_imSendImageQuality >= 0 && m_imSendImageQuality < fsm_imUploadImageSize_size.length){
			return fsm_imUploadImageSize_size[m_imSendImageQuality];
		}else{
			m_imSendImageQuality = 0;
		}
		
		return null;
	}
	
	public int getIMChatMsgHistory(){
		if(m_imChatMsgHistory < 0 || m_imChatMsgHistory >= fsm_imChatMsgHistory.length){
			m_imChatMsgHistory = 0;
		}
		
		return fsm_imChatMsgHistory[m_imChatMsgHistory];
	}
	
	final static String fsm_imStoreImageVoiceDir = uploadFileScreen.fsm_rootPath_default + "YuchBerry/ChatHistory/";
	boolean m_imStoreImageDirInit = false;
	public String getIMStoreImageVoicePath()throws Exception{
		
		if(!m_imStoreImageDirInit){
			makeDir(uploadFileScreen.fsm_rootPath_default + "YuchBerry/");
			makeDir(fsm_imStoreImageVoiceDir);
			
			m_imStoreImageDirInit = true;
		}
		
		return fsm_imStoreImageVoiceDir;
		
	}
	
}

