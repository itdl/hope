package hope.analyzer.analyzer;

import hope.analyzer.model.KLineInfo;
import hope.analyzer.model.ResultInfo;
import hope.analyzer.model.Stock;
import hope.analyzer.util.Utils;

import java.util.List;

//n天内有涨停，现价跌幅在m 的股票
public class SuddentIncreaseAnalyzer extends AbstractStockAnalyzer {

    // consider how many days before now.
    int daysToNow = 10;

    // increase range
    double increase = 9;

    // decrease now
    double decrease = -3.5;

    public SuddentIncreaseAnalyzer() {
    }

    public boolean analyze(ResultInfo resultInfo, Stock stock) {
        boolean ok = false;
        List<KLineInfo> infos = stock.getkLineInfos();
        if (infos.size() <= daysToNow) {
            // 元数据天数要求大于考察天数
            return ok;
        }

        KLineInfo current = infos.get(infos.size() - 1);

        for (int i = infos.size() - daysToNow - 1; i < infos.size(); i++) {
            KLineInfo toCheck = infos.get(i);

            double increaseReage = (toCheck.getClose() - toCheck.getOpen())
                    / toCheck.getOpen();
            double percent = (current.getClose() - toCheck.getClose())
                    / toCheck.getClose();

            boolean condition1 = increaseReage * 100 >= increase;

            boolean condition2 = percent * 100 <= decrease;

            if (condition1 && condition2) {
                String msg = format(stock, current, toCheck);
                resultInfo.appendMessage(msg);
                ok = true;
                break;
            }
        }
        return ok;
    }

    public String format(Stock stock, KLineInfo current, KLineInfo check) {
        StringBuilder sb = new StringBuilder();
        sb.append(stock.getCode()).append("  ").append(stock.getName())
                .append("\n");
        sb.append("时间：").append(check.getDate()).append("\n");
        sb.append(
                "跌幅："
                        + Utils.double2Percentage((current.getClose() - check
                        .getClose()) / check.getClose())).append("\n");
        sb.append("现价: ").append(current.getClose()).append("\n\r");
        return (sb.toString());
    }

    public String getDescription() {
        return daysToNow + "天之内有近似涨停，目前跌幅大于 " + decrease + "% \n\r";
    }

    public int getDaysToNow() {
        return daysToNow;
    }

    public void setDaysToNow(int daysToNow) {
        this.daysToNow = daysToNow;
    }


}
