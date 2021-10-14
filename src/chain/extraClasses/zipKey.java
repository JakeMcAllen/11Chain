package chain.extraClasses;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.json.simple.JSONArray;


public class zipKey {
	
	public static PrivateKey deZipPrivateKey ( JSONArray privateJSA ) {
		
		try {
			KeyFactory keyFactoryPtK = KeyFactory.getInstance("RSA");
			
			byte[] bytesPtK = new byte[privateJSA.size()];
			
	        for (int i = 0; i < privateJSA.size(); i++) 
	        {
	        	bytesPtK[i] = ( (Long) privateJSA.get(i) ).byteValue();
	        }
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec( bytesPtK );
			return keyFactoryPtK.generatePrivate( privateKeySpec );
			
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}
	
	public static PublicKey deZipPublicKey ( JSONArray publicJSA ) {
		
		try {
			KeyFactory keyFactoryPcK = KeyFactory.getInstance("RSA");

			byte[] bytesPcK = new byte[publicJSA.size()];
			
	        for (int i = 0; i < publicJSA.size(); i++) 
	        {
	        	bytesPcK[i] = ( (Long) publicJSA.get(i) ).byteValue();
	        }
	        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec( bytesPcK );
			return keyFactoryPcK.generatePublic( publicKeySpec );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static PrivateKey deZipPrivateKey ( org.json.JSONArray privateJSA ) {
		
		try {
			KeyFactory keyFactoryPtK = KeyFactory.getInstance("RSA");
			
			byte[] bytesPtK = new byte[privateJSA.length()];
			
	        for (int i = 0; i < privateJSA.length(); i++) 
	        {   
	        	bytesPtK[i] = Integer.valueOf( privateJSA.get(i).toString() ).byteValue();
	        }
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec( bytesPtK );
			return keyFactoryPtK.generatePrivate( privateKeySpec );
			
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}
	
	public static PublicKey deZipPublicKey ( org.json.JSONArray publicJSA ) {
		
		try {
			KeyFactory keyFactoryPcK = KeyFactory.getInstance("RSA");

			byte[] bytesPcK = new byte[publicJSA.length()];
			
	        for (int i = 0; i < publicJSA.length(); i++) 
	        {
	        	bytesPcK[i] = Integer.valueOf( publicJSA.get(i).toString() ).byteValue();
	        }
	        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec( bytesPcK );
			return keyFactoryPcK.generatePublic( publicKeySpec );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static byte[] zipPrivateKey (PrivateKey prvKey) {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpecPtK = new PKCS8EncodedKeySpec( prvKey.getEncoded() );
		return pkcs8EncodedKeySpecPtK.getEncoded();
	}
	
	public static byte[] zipPublicKey (PublicKey pbcKey) {
		X509EncodedKeySpec pkcs8EncodedKeySpecPcK = new X509EncodedKeySpec( pbcKey.getEncoded() );
		return pkcs8EncodedKeySpecPcK.getEncoded();
	}
	
}
