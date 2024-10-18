package rdt;

public interface Receiver
{
    /**
     * 接收文件的初始化方法
     * @param fileName 文件名
     * @param pktNum 包的个数
     */
    void receiveFile(String fileName,int pktNum);

    /**
     * 接收数据的方法
     * @param data 接收到的数据
     */
    void receiveData(byte[] data);

    /**
     * 发送序号为seqNum的ACK
     * @param seqNum 序号
     */
    void sendACK(int seqNum);

    /**
     * 判断是否接收完毕
     * @return true表示接收完毕，false表示未接收完毕
     */
    boolean isFinished();
}
