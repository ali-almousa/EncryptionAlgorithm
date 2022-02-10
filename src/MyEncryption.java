
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @(#)MyEncryption.java
 * @author Ali Almousa 1945427
 *
 * This class perform encryption/decryption on any type of data
 * in files and output the encrypted/decrypted data to another file
 */
public class MyEncryption {
	protected char[] ASCIIcharacters = new char[95];		//All keyboard—ASCII characters (32 to 126)
	protected static int key;								//The encryption key string
	protected static MyEncryption encrypter;				//Object of MyEncryption to access all enc/dec methods				
	protected static File sourceFile;						//File to read from
	protected static File destinationFile;					//File to write to
	/**
	 * 
	 * @param keySelected
	 * 
	 * Constructor that creates an object (encrypter/decrypter) with a specific key
	 */
	public MyEncryption(String keySelected) {
		for (int i = 0; i < ASCIIcharacters.length; i++) {
			ASCIIcharacters[i] = (char)(32 + i);
		}
		
		setIntKey(keySelected);
	}
	
	/**
	 * 
	 * @param keySelected
	 * 
	 * Calculates and sets the value of key given the key string
	 */
	private void setIntKey(String keySelected) {
		char[] keyToChars = keySelected.toCharArray();
		int intKey = 0;
		//adding the ASCII value of each character multiplied by its position in the key string
		for (int i = 0; i < keyToChars.length; i++) {
			intKey += keyToChars[i] * (i + 1);		
		}
		
		key = intKey;
	}

	/**
	 * 
	 * @param x
	 * @return the index of x in ASCIIcharacters
	 */
	private int getIndexOf(char x) {
		for (int i = 0; i < ASCIIcharacters.length; i++) {
			if (ASCIIcharacters[i] == x) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 
	 * @param text (readable)
	 * @return an encrypted string (unreadable)
	 */
	private String encrypt(String text) {
		char[] msg = text.toCharArray();
		
		for (int i = 0; i < msg.length; i++) {
			int j = getIndexOf(msg[i]);
			int l = (j + key % ASCIIcharacters.length) % ASCIIcharacters.length;
			msg[i] = ASCIIcharacters[l];
		}
		
		return new String(msg);
		
	}

	/**
	 * 
	 * @param c
	 * @return an encrypted char 
	 * 
	 * an overloaded method that takes char instead of a string
	 */
	public char encrypt(char c) {
		
		int j = getIndexOf(c);
		int l = (j + key % ASCIIcharacters.length) % ASCIIcharacters.length;
		
		return ASCIIcharacters[l];
		
	}
	
	/**
	 * 
	 * @param text (unreadable)
	 * @return a decrypted string (readable)
	 */
	public String decrypt(String text) {
		char[] msg = text.toCharArray();
		
		for (int i = 0; i < msg.length; i++) {
			int j = getIndexOf(msg[i]);
			int l = (ASCIIcharacters.length + j - key % ASCIIcharacters.length) % ASCIIcharacters.length;
			msg[i] = ASCIIcharacters[l];
		}
		
		return new String(msg);
		
	}
	
	/**
	 * 
	 * @param c
	 * @return an decrypted char 
	 * 
	 * an overloaded method that takes char instead of a string
	 */
	public char decrypt(char c) {
		
		int j = getIndexOf(c);
		int l = (ASCIIcharacters.length + j - key % ASCIIcharacters.length) % ASCIIcharacters.length;
		
		return ASCIIcharacters[l];
		
	}
	
	/**
	 * 
	 * @param sourcePath
	 * @param destinationPath
	 * @throws IOException
	 * 
	 * Reads the content of the source file, applies base64 encoding then encrypts the data
	 * and finally writes the resultant data to the destination file 
	 */
	public void encryptFile(File sourcePath, File destinationPath) throws IOException {

		byte[] PDF2Bytes = Files.readAllBytes(Paths.get(sourcePath.getAbsolutePath()));
		String PDF2EncodedB64 = java.util.Base64.getEncoder().encodeToString(PDF2Bytes);
     	String encrypted = encrypt(PDF2EncodedB64);
		

     	try(FileWriter fos = new FileWriter(destinationPath);){
 			fos.write(encrypted);
     	}
	}
	
	/**
	 * 
	 * @param sourcePath
	 * @param destinationPath
	 * @throws IOException
	 * 
	 * Reads the content of the source file, applies base64 decoding then decrypts the data
	 * and finally writes the resultant data to the destination file 
	 */
	public void decryptFile(File sourcePath, File destinationPath) throws IOException {
		
		byte[] encrypted = Files.readAllBytes(Paths.get(sourcePath.getAbsolutePath()));
		String encryptedStr = new String(encrypted);
		String decrypted = decrypt(encryptedStr);
		byte[] decoded = java.util.Base64.getDecoder().decode(decrypted.getBytes());

		try(FileOutputStream fos = new FileOutputStream(destinationPath);){
			fos.write(decoded);
		}
	}
	
	/**
	 * 
	 * @param args (dec/enc sourceFile destinationFile key)
	 * @throws IOException
	 * 
	 * The main method reads the args and check the validity of the inputs
	 * and finally perform the encryption/decryption
	 */
	public static void main(String[] args) throws IOException {
		
		//in case the number of parameters is less than 4,
		//print an error message and exit.
		if (args.length < 4) {
			System.out.println("The number of arguments shouldn't be less than 4");
			System.exit(0);
		}
		
		encrypter = new MyEncryption(args[3]);

		//in case the source file does not exist,
		//print an error message and exit.
		sourceFile = new File(System.getProperty("user.dir") + "\\" + args[1]);
		if (!sourceFile.exists()) {
			System.out.println("The source file is not available!");
			System.out.printf("If you are running this app from the command line then make sure that %s exists in the same directory of MyEncryption.java file", args[1]);
			System.out.printf("If you are running this app from the editor then make sure that %s exists in the same directory of the project inside the workspace", args[1]);
			System.exit(1);
		}
		
		//read and create the destination file
		//if not successful then print an error message
		destinationFile = new File(System.getProperty("user.dir") + "\\" + args[2]);
		if (!destinationFile.createNewFile()) {
			System.out.println("the destination file can't be created!");
			System.exit(2);
		}
		
		//check wither the first arg is enc or dec
		//and call methods accordingly
		try {
			if (args[0].equals("enc")) {
				encrypter.encryptFile(sourceFile, destinationFile);
			}
			else {
				encrypter.decryptFile(sourceFile, destinationFile);
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("make sure you are using the same key used for encrypting!");
		}
		
		

	}
	
}
