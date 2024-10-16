package rdt;

import java.net.DatagramSocket;

public class GBN implements RDT
{

    @Override
    public int getSeqnum(byte[] pkt)
    {
        if (pkt.length < 4) {
            throw new IllegalArgumentException("数据包长度不足，无法读取序号.");
        }

        // 从前4个字节读取序号

        return ((pkt[0] & 0xFF) << 24) |
                ((pkt[1] & 0xFF) << 16) |
                ((pkt[2] & 0xFF) << 8) |
                (pkt[3] & 0xFF);
    }

    @Override
    public byte[] getData(byte[] pkt)
    {
        // 确保数据包长度大于4字节，前4字节为序号
        if (pkt.length <= 4) {
            return new byte[0]; // 如果数据包没有足够长度，返回空数组
        }

        // 创建新的数组来存储去掉序号后的数据
        byte[] data = new byte[pkt.length - 4];

        // 将 pkt 从索引 4 开始的部分复制到 data 数组中
        System.arraycopy(pkt, 4, data, 0, data.length);

        return data;
    }

    @Override
    public byte[] addSeqnum(byte[] data, int seqnum, int bytesRead)
    {
        // 创建新的数据包（4字节序号 + 实际读取的字节数数据）
        byte[] packet = new byte[4 + bytesRead];

        // 将序号存入packet的前4字节
        packet[0] = (byte) (seqnum >> 24);
        packet[1] = (byte) (seqnum >> 16);
        packet[2] = (byte) (seqnum >> 8);
        packet[3] = (byte) (seqnum);

        // 将读取到的文件内容复制到packet的后续字节
        System.arraycopy(data, 0, packet, 4, bytesRead);

        return packet;
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
        // 检查字节数组的长度是否足够
        if (pkt.length < 4) {
            return false; // 长度不足，无法是ACK
        }
        // 将字节数组转换为字符串
        String packetStr = new String(pkt);
        // 检查最后几个字符是否为 ":ACK"
        return packetStr.endsWith(":ACK");
    }
}
