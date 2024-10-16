package rdt;

import java.net.DatagramSocket;

public class GBNReceiver implements Receiver
{
    private final DatagramSocket socket;

    public GBNReceiver(DatagramSocket socket)
    {
        this.socket = socket;
    }


    @Override
    public void receiveFile(String fileName, int pktNum)
    {

    }

    @Override
    public void receiveData(byte[] data)
    {

    }

    @Override
    public void sendACK(int seqNum)
    {

    }
}
