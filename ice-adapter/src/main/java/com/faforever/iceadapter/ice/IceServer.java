package com.faforever.iceadapter.ice;

import com.faforever.iceadapter.IceAdapter;
import lombok.Data;
import org.ice4j.TransportAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class IceServer {
  private List<TransportAddress> stunAddresses = new ArrayList<>();
  private List<TransportAddress> turnAddresses = new ArrayList<>();
  private String turnUsername = "";
  private String turnCredential = "";
  private CompletableFuture<OptionalDouble> roundTripTime = CompletableFuture.completedFuture(OptionalDouble.empty());

  // Host is a bracketed IPv6 literal, a hostname / IPv4 address, or (legacy) a bare IPv6 literal.
  // Only the bare IPv6 form may contain ':', otherwise "host:port" gets swallowed as the host.
  public static final Pattern urlPattern = Pattern.compile("(?<protocol>stun|turn):(?<host>\\[[0-9a-fA-F:.]+\\]|[\\w.-]+|[0-9a-fA-F:]+)(:(?<port>\\d+))?(\\?transport=(?<transport>(tcp|udp)))?");

  /** The matched host, without the brackets of an IPv6 literal. */
  public static String hostOf(Matcher matcher) {
      String host = matcher.group("host");
      return host.startsWith("[") ? host.substring(1, host.length() - 1) : host;
  }

  public boolean hasAcceptableLatency() {
      OptionalDouble rtt = this.getRoundTripTime().join();
      return !rtt.isPresent() || rtt.getAsDouble() < IceAdapter.ACCEPTABLE_LATENCY;
  }
}
