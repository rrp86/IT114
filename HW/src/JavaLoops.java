public class JavaLoops {
	public static void main(String[] args) {
		int[] number = new int[] { 31, 42, 46, 48, 51, 57, 67, 72, 88, 97 };

		for (int i = 0; i < 10; i++) {
			if ((number[i] % 2) == 0) {
				System.out.println(number[i] + " ");
			}
		}
	}
}
