import org.dssc.chip8.Chip8;
public class Main {

    public static void main(String[] args) {
        Chip8 mychip = new Chip8(20);
        mychip.startChip8("pong.rom");

    }

}