package server;

<<<<<<< HEAD
import java.awt.Point;
=======
>>>>>>> ad6539c337c0f7fbed6b72a17dafb4e21fa039e9
import java.io.Serializable;

public class Payload implements Serializable {

	/**
	 * baeldung.com/java-serial-version-uid
	 */
<<<<<<< HEAD
	private static final long serialVersionUID = -6687715510484845707L;

	private String clientName;// ~2 bytes per character
=======
	private static final long serialVersionUID = -6687715510484845706L;

	private String clientName;
>>>>>>> ad6539c337c0f7fbed6b72a17dafb4e21fa039e9

	public void setClientName(String s) {
		this.clientName = s;
	}

	public String getClientName() {
		return clientName;
	}

<<<<<<< HEAD
	private String message;// ~2 bytes per character
=======
	private String message;
>>>>>>> ad6539c337c0f7fbed6b72a17dafb4e21fa039e9

	public void setMessage(String s) {
		this.message = s;
	}

	public String getMessage() {
		return this.message;
	}

<<<<<<< HEAD
	private PayloadType payloadType;// 4 bytes
=======
	private PayloadType payloadType;
>>>>>>> ad6539c337c0f7fbed6b72a17dafb4e21fa039e9

	public void setPayloadType(PayloadType pt) {
		this.payloadType = pt;
	}

	public PayloadType getPayloadType() {
		return this.payloadType;
	}

<<<<<<< HEAD
	private int number;// 4 bytes
=======
	private int number;
>>>>>>> ad6539c337c0f7fbed6b72a17dafb4e21fa039e9

	public void setNumber(int n) {
		this.number = n;
	}

	public int getNumber() {
		return this.number;
	}

<<<<<<< HEAD
	int x = 0;// 4 bytes
	int y = 0;// 4 bytes

	public void setPoint(Point p) {
		x = p.x;
		y = p.y;
	}

	public Point getPoint() {
		return new Point(x, y);
	}

	// added so two sets of x,y could be sent
	int x2 = 0;// 4 bytes
	int y2 = 0;// 4 bytes

	public void setPoint2(Point p) {
		x2 = p.x;
		y2 = p.y;
	}

	boolean flag = false;// 1 bit

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean getFlag() {
		return flag;
	}

	public Point getPoint2() {
		return new Point(x2, y2);
	}

	@Override
	public String toString() {
		return String.format("Type[%s], Number[%s], Message[%s], P1[%s], P2[%s]", getPayloadType().toString(),
				getNumber(), getMessage(), getPoint(), getPoint2());
=======
	@Override
	public String toString() {
		return String.format("Type[%s], Number[%s], Message[%s]", getPayloadType().toString(), getNumber(),
				getMessage());
>>>>>>> ad6539c337c0f7fbed6b72a17dafb4e21fa039e9
	}
}