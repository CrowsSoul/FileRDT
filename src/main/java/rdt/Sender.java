package rdt;

public interface Sender
{
    /**
     * 发送文件的初始化方法
     * @param fileName 文件名
     * @param pktNum 分包数量
     */
    void sendFile(String fileName,int pktNum);

    /**
     * 发送数据
     * @param data 数据
     */
    void sendData(byte[] data);

    /**
     * 接收ACK
     * @param ack ACK消息
     */
    void receiveACK(byte[] ack);

    /**
     * 判断是否已满
     * @return 是否已满
     */
    boolean isFull();
}
