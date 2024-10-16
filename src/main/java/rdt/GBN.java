package rdt;

import java.net.DatagramSocket;

public class GBN implements RDT
{
    @Override
    public int getSeqnum(byte[] pkt)
    {
        return 0;
    }

    @Override
    public byte[] getData(byte[] pkt)
    {
        return new byte[0];
    }

    @Override
    public byte[] addSeqnum(byte[] data, int seqnum)
    {
        return new byte[0];
    }

    @Override
    public Sender createSender(DatagramSocket socket)
    {
        return new GBNSender(socket);
    }

    @Override
    public Receiver createReceiver(DatagramSocket socket)
    {
        return new GBNReceiver(socket);
    }

    @Override
    public boolean isACK(byte[] pkt)
    {
        return false;
    }
}
