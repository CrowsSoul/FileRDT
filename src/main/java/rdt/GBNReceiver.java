package rdt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class GBNReceiver implements Receiver
{
    protected final DatagramSocket socket;
    protected String fileName;
    protected int totalPkts;
    protected int expectedSeqNum;

    public GBNReceiver(DatagramSocket socket)
    {
        this.socket = socket;
    }


    @Override
    public void receiveFile(String fileName, int pktNum)
    {
        this.fileName = fileName;
        this.totalPkts = pktNum;
        this.expectedSeqNum = 0;
    }

    @Override
    public void receiveData(byte[] data)
    {
        int seqNum = new GBN().getSeqnum(data);
        if(seqNum == expectedSeqNum)
        {
            sendACK(seqNum);
            expectedSeqNum++;
            System.out.println("====确认接收数据包:"+seqNum+"====");
        }
        else
        {
            System.out.println("====收到重传数据包:"+seqNum+"====");
            sendACK(expectedSeqNum-1);
        }
    }

    @Override
    public void sendACK(int seqNum)
    {
        // 将 seqNum 转换为字节数组
        byte[] seqNumBytes = ByteBuffer.allocate(4).putInt(seqNum).array();

        // 构建 ":ACK" 字符串的字节数组
        String ackMessage = ":ACK";
        byte[] ackBytes = ackMessage.getBytes();

        // 创建一个新的字节数组用于存储完整的数据包
        byte[] ackPacket = new byte[seqNumBytes.length + ackBytes.length];

        // 将 seqNum 的字节放入 ackPacket 中
        System.arraycopy(seqNumBytes, 0, ackPacket, 0, seqNumBytes.length);

        // 将 ":ACK" 的字节放入 ackPacket 中
        System.arraycopy(ackBytes, 0, ackPacket, seqNumBytes.length, ackBytes.length);

        // 发送 ackPacket
        try
        {
            socket.send(new DatagramPacket(ackPacket,ackPacket.length));
        } catch (IOException e)
        {
            System.out.println("====ACK发送失败====");
        }
    }

    @Override
    public boolean isFinished()
    {
        if(expectedSeqNum==totalPkts+1)
        {
            System.out.println("====所有数据包均接收完毕！====");
            return true;
        }
        return false;
    }
}
