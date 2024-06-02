import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Main {

    public static void main(String[] args){
        String inputFilePath;

        if(Objects.equals(args[0], "0")){
            inputFilePath = getJarPath();
        }else{
            inputFilePath = args[0];
        }

        if(!(new File(inputFilePath)).exists())
            System.out.println("File isn't exists, Pleas input correct file path");

        try {
            byte[] sha256 = calculateSHA256(inputFilePath);
            System.out.println("SHA256: " +  bytesToHex(sha256));
        }catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    private static String getJarPath() {
        String path = Main.class.getProtectionDomain().getCodeSource()
                .getLocation().getFile();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {}

        File jarFile = new File(path);

        File parent = jarFile.getParentFile();
        return parent.getAbsolutePath()+"\\"+jarFile.getName();
    }


    public static byte[] calculateSHA256(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (
                FileInputStream fis = new FileInputStream(filePath);
                FileChannel channel = fis.getChannel();
                DigestInputStream ignored = new DigestInputStream(fis, digest)) {

            ByteBuffer buffer = ByteBuffer.allocate(8192); // 8 KB buffer
            while (channel.read(buffer) != -1) {
                buffer.flip();
                digest.update(buffer);
                buffer.clear();
            }

            return digest.digest();
        }
    }


    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }


}