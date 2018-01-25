package lightlib.sdf.encrypt;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.NetworkInterface;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;


public class Security {
    public static void main(String[] args) {

//        testGenerate();

        try {
            testMatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void testGenerate() throws Exception {
        KeyPair keyPair = genKeyPair(1024);

        //获取公钥，并以base64格式打印出来
        PublicKey publicKey = keyPair.getPublic();
        System.out.println("公钥：" + new String(Base64.getEncoder().encode(publicKey.getEncoded())));

        //获取私钥，并以base64格式打印出来
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println("私钥：" + new String(Base64.getEncoder().encode(privateKey.getEncoded())));

        //公钥加密
        String encrypted = encrypt("helloWorld", publicKey);
        System.out.println("加密后：" + encrypted);

        //私钥解密
        String decrypted = decrypt(encrypted, privateKey);
        System.out.println("解密后：" + decrypted);
    }

    private static void testMatch() throws IOException {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8gqqKndF6BQXvhjOVtKPYyGxES1+IjIg9AjPNb+5V9I+zs9Ax5MSmkHNnqtFemaA4rF+FW8ko5hdtmTq37MhjrJKuvYKDObZ4C3M9WhqHhhHoBn8Lp2FXGmKVZXfEXjl7o0Z2qjgcDyExEToXxXifbqEF9XtS1i7x/7JOUDFGnQIDAQAB";
        String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALyCqoqd0XoFBe+GM5W0o9jIbERLX4iMiD0CM81v7lX0j7Oz0DHkxKaQc2eq0V6ZoDisX4VbySjmF22ZOrfsyGOskq69goM5tngLcz1aGoeGEegGfwunYVcaYpVld8ReOXujRnaqOBwPITEROhfFeJ9uoQX1e1LWLvH/sk5QMUadAgMBAAECgYAKL3Exfid0VVMlxvWAkDsOGn4nDn+B7D3sNfNAlVymTkl5ZyzHlVm+ui1SG5umZdyPU1jx5qyuxgfcBxxyFZwaFqaL/GJ5CEhXXIc7FtetebSjsQ8SVORp7JKYjQdo/8I/GF3rSbKBvYEOyOTEfFeBDlUuFgAO+EXsR/jKTzQUzQJBAPprb0SOmY4nFXtEO1XXxNxcQ30FJ2gBB/ela7Jo3D2hDoYvlIJU0t1dDo1+2lVn3C5hO84icFjiw3GiOTiY5osCQQDAtg8KBIK9B1mJIMsRAHEoXR+tySwKQdBB3GLEoQPN8YFia5EjyCSvJFcBRQI4LnKUmfwY4Kig8n6qnVQP+dR3AkEA+LFLTj2EGB2Ogt2PQ8BF/EYQrA87RFmJzRJWM1daKkZRg0eraAfPZhGtiy4IrLq5esILv7qJ8mw5hxeBVkja4wJBALYTTWJJoLy4lP/a3AHUSRW55pRr3hBS3lFbyHW/K7kI1RYIS7ljAEX6L7ojWVV7jQaZ9nYKzUhD1SmePC5b/UECQQC0uc0uwUgbkCqTzq5Ra6pGEQD1CLqjqursQs5VSqIXAtrd9tEUvMFuEskwpLM3AkibairzGPdNm6tvdUbC4H65";

        System.out.println(encrypt(publicKey));
        System.out.println(decrypt(privateKey));
    }

    //生成密钥对
    protected static KeyPair genKeyPair(int keyLength) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keyLength);
        return keyPairGenerator.generateKeyPair();
    }

    //将base64编码后的公钥字符串转成PublicKey实例
    protected static PublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    //将base64编码后的私钥字符串转成PrivateKey实例
    protected static PrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    //公钥加密
    private static String encrypt(String content, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] data = cipher.doFinal(content.getBytes());
        return new String(Base64.getEncoder().encode(data));
    }

    //私钥解密
    private static String decrypt(String content, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] data = cipher.doFinal(Base64.getDecoder().decode(content));
        return new String(data);
    }

    public static String decrypt(String privateKey) {
        File encryptedDataFile = new File(System.getProperty("user.home"), ".sdf-license");
        String encryptedData = null;
        if (encryptedDataFile.exists() && encryptedDataFile.canRead()) {
//            String pubData =
            StringBuffer sb = new StringBuffer();
            try {
                FileReader fr = new FileReader(encryptedDataFile);
                char[] buf = new char[64];
                int p = 0;
                while ((p = fr.read(buf)) != -1) {
                    sb.append(buf, 0, p);
                }
                fr.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            encryptedData = sb.toString();
        }

        String content = null;
        if (encryptedData != null) {
            try {
                PrivateKey pk = getPrivateKey(privateKey);
                content = decrypt(encryptedData, pk);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }

        if (content != null) {
            String[] data = content.split("\n");
            if (data != null && data.length == 2) {
                if (data[0].equalsIgnoreCase(getMac())) {
                    return data[1];
                }
            }
        }
        return null;
    }

    public static String encrypt(String publicKey) throws IOException {
        File encryptedDataFile = new File(System.getProperty("user.home"), ".sdf-license");
        if (!encryptedDataFile.exists()) {
            encryptedDataFile.createNewFile();
        }
        PublicKey pk = null;
        try {
            pk = getPublicKey(publicKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        String data = null;
        try {
            data = encrypt(getMac() + "\nhelloWorld", pk);

            FileWriter fr = new FileWriter(encryptedDataFile);
            fr.write(data);
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return data;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder sb = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int vv : src) {//int i = 0; i < src.length; i++
            int v = vv & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);//
        }
        return sb.toString();
    }

    private final static String getMac() {
        List<String> list = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces();
            while (el.hasMoreElements()) {
                byte[] mac = el.nextElement().getHardwareAddress();
                if (mac == null)
                    continue;
                list.add(bytesToHexString(mac));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.length() != o2.length()) {
                    return o1.length() - o2.length();
                }

                int p = 0, s = o1.length();
                while (p < s) {
                    int r = o1.charAt(p) - o2.charAt(p);
                    if (r == 0) {
                        p++;
                    } else {
                        return r;
                    }
                }
                return 0;
            }
        });
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
