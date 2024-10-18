package rdt;

import java.net.DatagramSocket;

public class SR extends GBN
{
    @Override
    public Sender createSender(DatagramSocket socket)
    {
        return new SRSender(socket);
    }

    @Override
    public Receiver createReceiver(DatagramSocket socket)
    {
        return new SRReceiver(socket);
    }
}
