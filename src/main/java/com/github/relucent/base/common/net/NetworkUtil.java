package com.github.relucent.base.common.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.net.ServerSocketFactory;

import com.github.relucent.base.common.constant.ArrayConstant;
import com.github.relucent.base.common.logging.Logger;

/**
 * 网络信息工具类
 */
public class NetworkUtil {

    private static final Logger LOGGER = Logger.getLogger(NetworkUtil.class);

    /** 回送地址(本机地址 ) */
    public static final String LOCALHOST_IPV4 = "127.0.0.1";

    /** 通配地址(所有主机) */
    public static final String ANYHOST_IPV4 = "0.0.0.0";

    /** 限制广播地址(所有主机) */
    public static final String BROADCAST_IPV4 = "255.255.255.255";

    /** 最小端口号 */
    public static final int MIN_PORT = 0;
    /** 最大端口号 */
    public static final int MAX_PORT = 65535;

    /** IPv4 正则 */
    private static final Pattern IPV4_PATTERN = Pattern.compile(//
            "^(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})(\\.(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})){3}$"//
    );

    /** IPV6最大十六进制组数 */
    private static final int IPV6_MAX_HEX_GROUPS = 8;
    /** IPV6最大十六进制每组数字数 */
    private static final int IPV6_MAX_HEX_DIGITS_PER_GROUP = 4;
    /** 最大无符号短整型数 */
    private static final int MAX_UNSIGNED_SHORT = 0xffff;
    /** 基数16 */
    private static final int BASE_16 = 16;

    /** 有效主机名片段部分部分（字母或数字） */
    private static final Pattern REG_NAME_PART_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9-]*$");

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected NetworkUtil() {
    }

    /**
     * 获得本机网卡物理地址
     * @return 网卡物理地址(数组)
     */
    public static final String[] getMacAddress() {
        Set<String> macSet = new LinkedHashSet<String>();
        try {
            for (Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces(); el.hasMoreElements();) {
                NetworkInterface networkInterface = el.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac == null || mac.length == 0) {
                    continue;
                }
                macSet.add(toMacString(mac));
            }
        } catch (SocketException e) {
            LOGGER.error("?", e);
        }
        return macSet.toArray(new String[macSet.size()]);
    }

    /**
     * 获得本机IP(v4)地址
     * @return 本机IP(v4)地址
     */
    public static final String getHostAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> addrs = networkInterface.getInetAddresses(); addrs.hasMoreElements();) {
                    String ip = addrs.nextElement().getHostAddress();
                    if (isValidIPv4(ip)) {
                        return ip;
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            LOGGER.error("?", e);
            return "";
        }
    }

    /**
     * 获得本机 MAC地址
     * @return 本机MAC地址
     */
    public static final byte[] getHardwareAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> addrs = networkInterface.getInetAddresses(); addrs.hasMoreElements();) {
                    String ip = addrs.nextElement().getHostAddress();
                    if (isValidIPv4(ip)) {
                        return networkInterface.getHardwareAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LOGGER.error("?", e);
        }
        return new byte[0];
    }

    /**
     * 地址转换为字符串
     * @param mac 地址
     * @return MAC地址字符串
     */
    public static String toMacString(byte[] mac) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, length = mac.length; i < length; i++) {
            if (i != 0) {
                builder.append("-");
            }
            String hex = Integer.toHexString(mac[i]);
            switch (hex.length()) {
            case 0:
                builder.append("00");// 00
                break;
            case 1:
                builder.append("0");// 0+
                break;
            default:
                builder.append(hex.substring(Math.max(hex.length() - 2, 0)));// ++
            }
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 检测本地端口可用性<br>
     * @param port 被检测的端口
     * @return 是否可用
     */
    public static boolean isPortAvailable(int port) {
        // 给定的IP未在指定端口范围中
        if (!isValidPort(port)) {
            return false;
        }
        // TCP
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1,
                    InetAddress.getByName("localhost"));
            serverSocket.close();
        } catch (Exception ex) {
            return false;
        }
        // UDP
        try {
            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("localhost"));
            socket.close();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否是有效IPv4地址
     * @param ip IP地址
     * @return 如果是有效IPv4地址返回true,否则返回false
     */
    public static boolean isValidIPv4(String ip) {
        return (ip != null //
                && !ANYHOST_IPV4.equals(ip) //
                && !LOCALHOST_IPV4.equals(ip) //
                && !BROADCAST_IPV4.equals(ip) //
                && IPV4_PATTERN.matcher(ip).matches());
    }

    /**
     * 判断是否是有效端口号(1～65535)
     * @param port 端口号
     * @return 如果是有效端口号返回true,否则返回false
     */
    public static boolean isValidPort(int port) {
        return MIN_PORT < port && port <= MAX_PORT;
    }

    /**
     * 判断是否是有效的IPv6地址
     * @param inet6Address 要验证的IP名称
     * @return 如果是一个有效的IPv6地址，返回true
     */
    public static boolean isValidIPv6(final String inet6Address) {
        final boolean containsCompressedZeroes = inet6Address.contains("::");
        if (containsCompressedZeroes && (inet6Address.indexOf("::") != inet6Address.lastIndexOf("::"))) {
            return false;
        }
        if ((inet6Address.startsWith(":") && !inet6Address.startsWith("::"))
                || (inet6Address.endsWith(":") && !inet6Address.endsWith("::"))) {
            return false;
        }
        String[] octets = inet6Address.split(":");
        if (containsCompressedZeroes) {
            final List<String> octetList = new ArrayList<>(Arrays.asList(octets));
            if (inet6Address.endsWith("::")) {
                octetList.add("");
            } else if (inet6Address.startsWith("::") && !octetList.isEmpty()) {
                octetList.remove(0);
            }
            octets = octetList.toArray(ArrayConstant.EMPTY_STRING_ARRAY);
        }
        if (octets.length > IPV6_MAX_HEX_GROUPS) {
            return false;
        }
        int validOctets = 0;
        int emptyOctets = 0; // consecutive empty chunks
        for (int index = 0; index < octets.length; index++) {
            final String octet = octets[index];
            if (octet.length() == 0) {
                emptyOctets++;
                if (emptyOctets > 1) {
                    return false;
                }
            } else {
                emptyOctets = 0;
                // IPv6 的一种特殊写法，IPv6 中嵌入 IPv4（IPv4-Mapped IPv6 Address 或 IPv4 Embedded in IPv6）
                if (index == octets.length - 1 && octet.contains(".")) {
                    if (!isValidIPv4(octet)) {
                        return false;
                    }
                    validOctets += 2;
                    continue;
                }
                if (octet.length() > IPV6_MAX_HEX_DIGITS_PER_GROUP) {
                    return false;
                }
                int octetInt = 0;
                try {
                    octetInt = Integer.parseInt(octet, BASE_16);
                } catch (final NumberFormatException e) {
                    return false;
                }
                if (octetInt < 0 || octetInt > MAX_UNSIGNED_SHORT) {
                    return false;
                }
            }
            validOctets++;
        }
        return validOctets <= IPV6_MAX_HEX_GROUPS && (validOctets >= IPV6_MAX_HEX_GROUPS || containsCompressedZeroes);
    }

    /**
     * 根据 RFC 3986 检查名称是否为有效主机名
     * @see "https://tools.ietf.org/html/rfc3986#section-3.2.2"
     * @param name 要验证的主机名
     * @return 是否有效主机名
     */
    public static boolean isRFC3986HostName(final String name) {
        final String[] parts = name.split("\\.", -1);
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() == 0) {
                return i == parts.length - 1;
            }
            if (!REG_NAME_PART_PATTERN.matcher(parts[i]).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据相关标准检查给定字符串是否为有效主机名 RFC 3986.
     * @see "https://tools.ietf.org/html/rfc3986#section-3.2.2"
     * @param name 要验证的主机名
     * @return 是否有效主机名
     */
    public static boolean isValidHostName(final String name) {
        return isValidIPv6(name) || isRFC3986HostName(name);
    }
}
