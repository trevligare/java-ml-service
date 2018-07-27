package org.trevligare.server;

import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.rs.RsJson;
import org.takes.rs.RsText;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.FkTypes;
import org.takes.facets.fork.TkFork;
import org.takes.Take;
import org.takes.Response;
import org.takes.Request;
import org.takes.misc.Href;
import org.takes.rq.RqHref;

import org.trevligare.HateThreatSentence;

import java.io.IOException;

public final class App {
  public static void main(final String... args) throws ClassNotFoundException, IOException {
    HateThreatSentence model = new HateThreatSentence("config.properties");
    int port = 8080;
    if (System.getenv("PORT") != null) port = Integer.parseInt(System.getenv("PORT"));
    new FtBasic(
      new TkFork(
        new FkRegex("/", "hello, world!"),
        new FkRegex("/identify-sentence", new HateThreatSentenceRequest(model))
      ), port
    ).start(Exit.NEVER);
  }

  public static class HateThreatSentenceRequest implements Take {
    HateThreatSentence model = null;
    public HateThreatSentenceRequest(HateThreatSentence model) {
      this.model = model;
    }
    @Override
    public Response act(final Request req) {
      try {
        Href href = new RqHref.Base(req).href();
        String sentence = href.param("sentence").iterator().next();
        double hateProb = model.isHateThreatSentence(sentence);
        return new RsText("{" +
            "\"sentence\":\"" + sentence.replace("\"", "\\\"") + "\"," +
            "\"is_hate_speech\":" + (hateProb > 0.5) + "," +
            "\"is_hate_speech_prob\":" + hateProb +
        "}");
      } catch (IOException ex) {}
      return null;
    }
  }
}
