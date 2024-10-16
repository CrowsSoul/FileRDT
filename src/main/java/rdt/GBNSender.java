package rdt;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class GBNSender implements Sender
{
    private final DatagramSocket socket;

    public GBNSender(DatagramSocket socket)
    {
        this.socket = socket;
    }

    @Override
    public void sendFile(String fileName, int pktNum)
    {

    }

    @Override
    public void sendData(byte[] data)
    {

    }

    @Override
    public void receiveACK(byte[] ack)
    {

    }

    @Override
    public boolean isFull()
    {
        return false;
    }
}
