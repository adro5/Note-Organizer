package macro.noteorganizer;

import java.io.UnsupportedEncodingException;

class Decrypter {
    private String plainText;

    public Decrypter() {
        try {
            plainText = new String(decrypt(), "UTF-8");
        }
        catch (UnsupportedEncodingException ex) { System.err.println(ex.getMessage()); }
    }

    private byte[] decrypt() {
        byte[] plaintext = new byte[1];

        return plaintext;
    }

    public String getPlainText() { return plainText; }
}
