package qub;

public class FakeDNS implements DNS
{
    private final MutableMap<String,IPv4Address> hostToIPAddressMap;

    private FakeDNS()
    {
        this.hostToIPAddressMap = Map.create();
    }

    public static FakeDNS create()
    {
        return new FakeDNS();
    }

    public FakeDNS set(String host, IPv4Address ipAddress)
    {
        PreCondition.assertNotNullAndNotEmpty(host, "host");
        PreCondition.assertNotNull(ipAddress, "ipAddress");

        hostToIPAddressMap.set(host, ipAddress);
        return this;
    }

    @Override
    public Result<IPv4Address> resolveHost(String host)
    {
        PreCondition.assertNotNullAndNotEmpty(host, "host");

        return Result.create(() ->
        {
            IPv4Address result = IPv4Address.parse(host);
            if (result == null)
            {
                result = hostToIPAddressMap.get(host)
                    .convertError(NotFoundException.class, () -> new java.net.UnknownHostException("No such host is known (" + host + ")"))
                    .await();
            }
            return result;
        });
    }
}
