
package org.safehaus.kiskis.mgmt.server.ui.modules.monitor.view;

import com.vaadin.ui.Window;
import org.apache.commons.lang3.StringUtils;
import org.safehaus.kiskis.mgmt.server.ui.modules.monitor.service.Metric;
import org.safehaus.kiskis.mgmt.server.ui.modules.monitor.service.Query;
import org.safehaus.kiskis.mgmt.server.ui.modules.monitor.util.FileUtil;
import org.safehaus.kiskis.mgmt.server.ui.modules.monitor.util.JavaScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Chart {

    private final static Logger LOG = LoggerFactory.getLogger(Chart.class);

    private static final String CHART_TEMPLATE = FileUtil.getContent("js/chart.js");

    private JavaScript javaScript;

    private Poll poll;

    private String host;
    private Metric metric;

    Chart(Window window) {
        javaScript = new JavaScript(window);
        loadScripts();
    }

    private void loadScripts() {
        javaScript.loadFile("js/jquery.min.js");
        javaScript.loadFile("js/highcharts.js");
        javaScript.loadFile("js/global.js");
    }

    void load(String host, Metric metric) {
        LOG.info("host: {}; metric: {}", host, metric);

        if (host == null || metric == null) {
            return;
        }

        this.host = host;
        this.metric = metric;

        String data = Query.execute(host, metric.toString(), 25);

        if (StringUtils.isEmpty(data)) {
            return;
        }

        String chart = CHART_TEMPLATE
                .replace( "$mainTitle", String.format("%s / %s", host, metric) )
                .replace( "$yTitle", metric.getUnit() )
                .replace( "$data", data )
                .replace( "$data", data );

        javaScript.execute(chart);
        startPolling();
    }

    private void startPolling() {

        if (poll != null) {
            poll.interrupt();
        }

        poll = new Poll(this);
        poll.start();
    }

    void push() {
        String data = Query.execute(host, metric.toString(), 1);
        String script = String.format("setData(%s);", data);
        javaScript.execute(script);
    }

}
