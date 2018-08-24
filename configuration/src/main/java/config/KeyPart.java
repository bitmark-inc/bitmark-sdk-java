package config;

public enum KeyPart {

    PRIVATE_KEY(0x00), PUBLIC_KEY(0x01);

    private int value;

    KeyPart(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
