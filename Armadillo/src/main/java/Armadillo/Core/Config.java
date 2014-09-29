package Armadillo.Core;


import java.util.HashMap;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import Armadillo.Core.Io.FileHelper;
import Armadillo.Core.Io.PathHelper;

public class Config 
{
	private final static String LOCATION = "Config";
	private String m_strConfigName;
	private boolean m_blnIsInitialized;
	private static Object m_locker = new Object();
	private Configuration m_config;
	private static HashMap<String, Config> m_instanceMap = new HashMap<String, Config>();
	private static String m_strClientName;
	public static final String strDnsName = NetworkHelper.getHostName();
	
	static
	{
        //String strDnsName = NetworkHelper.getHostName();
        
        m_strClientName = strDnsName + "%" +
        		RuntimeHelper.getCurrentJarName() + "%" +
        		RuntimeHelper.getProcessId();
	}
	
	public static String getClientName(){
		try{
			return m_strClientName;
		}
		catch(Exception ex){
			Logger.log(ex);
		}
		return "";
	}
	
	public static String getStringStatic(String strKey) {
		try {
			Class<?> callingClass = Config.class;
			return getConfig(callingClass).getStr(strKey);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public String getStr(String strKey) 
	{
		return m_config.getString(strKey);
	}

	public static Config getConfig(Class<?> callingClass) 
	{
		synchronized (m_locker) 
		{
			Config instanceConfig = m_instanceMap.get(callingClass.getName());
			if (instanceConfig == null) 
			{
				instanceConfig = new Config();
				instanceConfig.initialize(callingClass);
				m_instanceMap.put(callingClass.getName(), instanceConfig);
			}
			return instanceConfig;
		}
	}

	public void initialize(Class<?> classRef) 
	{
		try 
		{
			if (m_blnIsInitialized) 
			{
				return;
			}
			String strPackageName = classRef.getPackage().getName();
			m_strConfigName = PathHelper.combinePaths(LOCATION, strPackageName)
					+ ".xml";

			if (!FileHelper.exists(m_strConfigName)) 
			{
				throw new Exception("Config file not found [" + m_strConfigName
						+ "]");
			}
			getConfig();
			m_blnIsInitialized = true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	private void getConfig() 
	{
		try 
		{
			m_config = new XMLConfiguration(m_strConfigName);
		} 
		catch (ConfigurationException e) 
		{
			e.printStackTrace();
		}
	}

	public static String getHostName() {
		return strDnsName;
	}
}