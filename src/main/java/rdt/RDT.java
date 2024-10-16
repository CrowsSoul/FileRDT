package rdt;

import java.net.DatagramSocket;

public interface RDT
{
    /**
     * 对接收的包进行解析，获取分组序号
     * @param pkt 包
     * @return 分组序号
     */
    int getSeqnum(byte[] pkt);

    /**
     * 对接收的包进行解析，获取数据部分
     * @param pkt 包
     * @return 包的数据部分
     */
    byte[] getData(byte[] pkt);

    /**
     * 将序号与数据组合成完整的包
     * @param data 数据
     * @param seqnum 序号
     * @param bytesRead 已读取的字节数
     * @return 完整的包
     */
    byte[] addSeqnum(byte[] data,int seqnum,int bytesRead);

    /**
     * 创建发送端的工厂方法
     * @param socket 数据包套接字
     * @return 发送端的实例
     */
    public Sender createSender(DatagramSocket socket);

    /**
     * 创建接收端的工厂方法
     * @param socket 数据包套接字
     * @return 接收端的实例
     */
    public Receiver createReceiver(DatagramSocket socket);

    /**
     * 判断是否为ACK包
     * @param pkt 包
     * @return 是否为ACK包
     */
    boolean isACK(byte[] pkt);
}
