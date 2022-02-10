package com.snaps.common.utils.ui;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SnsFactory {
	private static final String TAG = SnsFactory.class.getSimpleName();

	private static SnsFactory uniqueInstance ;

	IFacebook 	facebook;
	IKakao 		kakao;
	IBetween    between;
	
	public enum eSocial {
		eFacebook, eKakao
	}

	public static SnsFactory getInstance()
	{ 
		if(uniqueInstance  ==null){//있는지 체크 없으면 
			uniqueInstance  = new SnsFactory (); //생성한뒤
		}

		return uniqueInstance  ;//성성자를 넘긴다.
	
	}
	public IFacebook queryInteface()
	{
		createfacebookfactory();
		return facebook;
	}
	
	public IKakao queryIntefaceKakao()
	{
		if( !Config.isSnapsSDK() ) createKakaofactory();
		return kakao;
	}
	
	public IBetween queryInterfaceBetween()
	{
		createBetweenfactory();
		return between;
	}
	
	private void createfacebookfactory()
	{
		try{
				final Class[] ctorParams = {String.class};
				Class klass;
					        
				 klass = Class.forName("com.snaps.facebook.utils.sns.FacebookUtil");
				 Constructor cons = klass.getConstructor(ctorParams);
					
				 boolean acces = cons.isAccessible();
				 
				 cons.setAccessible(true);
				 
				  Object [] param= {"test"};
				  Object obj ;
				 facebook = (IFacebook)cons.newInstance(param);
		
			}
		catch (NoSuchMethodException e)
		{
			Dlog.e(TAG, e);
					
		}
		catch (IllegalArgumentException e) 
		{
			Dlog.e(TAG, e);
				
		}
		catch (InvocationTargetException e)
		{
			Dlog.e(TAG, e);
				
		}
		catch (ClassNotFoundException e) 
		{
			Dlog.e(TAG, e);
			
		}
		catch(InstantiationException e)
		{
			Dlog.e(TAG, e);
			
		}
		catch(IllegalAccessException e)
		{
			Dlog.e(TAG, e);
		//    return null;
		
		}
		
	}
	private void createKakaofactory()
	{
		try{
				final Class[] ctorParams = {String.class};
				Class klass;
					        
				 klass = Class.forName("com.snaps.kakao.utils.kakao.KaKaoUtil");
				 Constructor cons = klass.getConstructor(ctorParams);
					
				 boolean acces = cons.isAccessible();
				 
				 cons.setAccessible(true);
				 
				  Object [] param= {"test"};
				  Object obj ;
				 kakao = (IKakao)cons.newInstance(param);
		
			}
		catch (NoSuchMethodException e)
		{
			Dlog.e(TAG, e);
					
		}
		catch (IllegalArgumentException e) 
		{
			Dlog.e(TAG, e);
				
		}
		catch (InvocationTargetException e)
		{
			Dlog.e(TAG, e);
				
		}
		catch (ClassNotFoundException e) 
		{
			Dlog.e(TAG, e);
			
		}
		catch(InstantiationException e)
		{
			Dlog.e(TAG, e);
			
		}
		catch(IllegalAccessException e)
		{
			Dlog.e(TAG, e);
		
		}
		catch (ClassCastException e) {
			Dlog.e(TAG, e);
		}
		
	}
	
	private void createBetweenfactory()
	{
		@SuppressWarnings("rawtypes")
		Class klass;
		try {
			klass = Class.forName("com.snaps.mobile.between.util.BetweenUtil");
			between = (IBetween) klass.newInstance();
		} catch (ClassNotFoundException e) {
			Dlog.e(TAG, e);
		} catch (InstantiationException e) {
			Dlog.e(TAG, e);
		} catch (IllegalAccessException e) {
			Dlog.e(TAG, e);
		}
	}

}
