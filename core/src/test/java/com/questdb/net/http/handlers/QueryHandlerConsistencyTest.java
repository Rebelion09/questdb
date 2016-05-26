/*******************************************************************************
 *    ___                  _   ____  ____
 *   / _ \ _   _  ___  ___| |_|  _ \| __ )
 *  | | | | | | |/ _ \/ __| __| | | |  _ \
 *  | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *   \__\_\\__,_|\___||___/\__|____/|____/
 *
 * Copyright (C) 2014-2016 Appsicle
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * As a special exception, the copyright holders give permission to link the
 * code of portions of this program with the OpenSSL library under certain
 * conditions as described in each individual source file and distribute
 * linked combinations including the program with the OpenSSL library. You
 * must comply with the GNU Affero General Public License in all respects for
 * all of the code used other than as permitted herein. If you modify file(s)
 * with this exception, you may extend this exception to your version of the
 * file(s), but you are not obligated to do so. If you do not wish to do so,
 * delete this exception statement from your version. If you delete this
 * exception statement from all source files in the program, then also delete
 * it in the license file.
 *
 ******************************************************************************/

package com.questdb.net.http.handlers;

import com.questdb.factory.JournalFactoryPool;
import com.questdb.iter.clock.MilliClock;
import com.questdb.net.http.ContextHandler;
import com.questdb.net.http.IOContext;
import com.questdb.net.http.ServerConfiguration;
import com.questdb.ql.parser.AbstractOptimiserTest;
import com.questdb.test.tools.TestChannel;
import com.questdb.test.tools.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryHandlerConsistencyTest extends AbstractOptimiserTest {
    private static final String QUERY1 = "GET /js?query=%27xyz%27&limit=0%2C1000&count=true HTTP/1.1\n" +
            "Host: localhost:9000\r\n" +
            "Connection: keep-alive\r\n" +
            "Accept: */*\r\n" +
            "X-Requested-With: XMLHttpRequest\r\n" +
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36\r\n" +
            "Referer: http://localhost:9000/index.html\r\n" +
            "Accept-Encoding: gzip, deflate, sdch\r\n" +
            "Accept-Language: en-US,en;q=0.8,ru;q=0.6\r\n" +
            "Cookie: _ga=GA1.1.825719031.1458963361\r\n" +
            "\r\n";

    @BeforeClass
    public static void setUp() throws Exception {
        QueryHandlerTest.generateJournal("xyz", 100);
    }

    @Test
    public void testCsvHandlerConsistency() throws Exception {
        testHandler(new CsvHandler(new JournalFactoryPool(factory.getConfiguration(), 1), new ServerConfiguration()));
    }

    @Test
    public void testCsvOutput() throws Exception {
        ContextHandler handler = new CsvHandler(new JournalFactoryPool(factory.getConfiguration(), 1), new ServerConfiguration());
        handler.setupThread();
        TestChannel channel = new TestChannel(QUERY1);
        String expected = "\"id\",\"x\",\"y\",\"z\",\"w\",\"timestamp\"\r\n" +
                "id0,0.0000000019,0.0000011892,-498,171,\"2015-03-12T00:00:00.000Z\"\r\n" +
                "id1,0.0066887384,0.0244944207,-339,12,\"2015-03-12T00:00:00.010Z\"\r\n" +
                "id2,-432.0000000000,0.2932029516,216,-197,\"2015-03-12T00:00:00.020Z\"\r\n" +
                "id3,768.0000000000,,-227,-464,\"2015-03-12T00:00:00.030Z\"\r\n" +
                "id4,7.0894718170,0.0000007417,353,228,\"2015-03-12T00:00:00.040Z\"\r\n" +
                "id5,920.6250000000,,197,0,\"2015-03-12T00:00:00.050Z\"\r\n" +
                "id6,13.7327423096,54.0429687500,-40,-247,\"2015-03-12T00:00:00.060Z\"\r\n" +
                "id7,12.9238665104,,-339,-319,\"2015-03-12T00:00:00.070Z\"\r\n" +
                "id8,384.0723876953,0.0005276347,10,2,\"2015-03-12T00:00:00.080Z\"\r\n" +
                "id9,0.0000000173,,-359,263,\"2015-03-12T00:00:00.090Z\"\r\n" +
                "id10,0.0115543692,0.0473926794,409,167,\"2015-03-12T00:00:00.100Z\"\r\n" +
                "id11,0.0011654748,0.0000000089,-211,-395,\"2015-03-12T00:00:00.110Z\"\r\n" +
                "id12,0.0000001961,-484.0000000000,356,124,\"2015-03-12T00:00:00.120Z\"\r\n" +
                "id13,0.5831662863,942.8994140625,247,1,\"2015-03-12T00:00:00.130Z\"\r\n" +
                "id14,-249.0703125000,-735.6542968750,288,-121,\"2015-03-12T00:00:00.140Z\"\r\n" +
                "id15,-607.9462890625,,327,-319,\"2015-03-12T00:00:00.150Z\"\r\n" +
                "id16,0.0004250098,0.0000015750,-311,-302,\"2015-03-12T00:00:00.160Z\"\r\n" +
                "id17,0.0000000921,-186.0000000000,,-113,\"2015-03-12T00:00:00.170Z\"\r\n" +
                "id18,0.0000000209,0.0154700107,191,-260,\"2015-03-12T00:00:00.180Z\"\r\n" +
                "id19,0.0608360972,0.0073713257,-168,355,\"2015-03-12T00:00:00.190Z\"\r\n" +
                "id20,768.0000000000,300.1632385254,31,282,\"2015-03-12T00:00:00.200Z\"\r\n" +
                "id21,770.3593750000,0.4610133618,-42,-332,\"2015-03-12T00:00:00.210Z\"\r\n" +
                "id22,0.0000002364,0.0002006101,-208,-190,\"2015-03-12T00:00:00.220Z\"\r\n" +
                "id23,0.1907100230,0.0004840687,410,-392,\"2015-03-12T00:00:00.230Z\"\r\n" +
                "id24,-663.0000000000,0.0000302110,-199,428,\"2015-03-12T00:00:00.240Z\"\r\n" +
                "id25,0.0000261224,-296.5444335938,-20,-320,\"2015-03-12T00:00:00.250Z\"\r\n" +
                "id26,-100.0000000000,-915.7968750000,,85,\"2015-03-12T00:00:00.260Z\"\r\n" +
                "id27,0.0000025266,,38,-213,\"2015-03-12T00:00:00.270Z\"\r\n" +
                "id28,0.0000320606,0.0006038445,243,-32,\"2015-03-12T00:00:00.280Z\"\r\n" +
                "id29,-352.0000000000,0.0000710208,172,-261,\"2015-03-12T00:00:00.290Z\"\r\n" +
                "id30,238.6328125000,134.6630859375,-379,-352,\"2015-03-12T00:00:00.300Z\"\r\n" +
                "id31,0.0000001943,0.0003494991,416,-97,\"2015-03-12T00:00:00.310Z\"\r\n" +
                "id32,0.0000000139,-1024.0000000000,-200,348,\"2015-03-12T00:00:00.320Z\"\r\n" +
                "id33,128.0000000000,0.0011758586,-20,392,\"2015-03-12T00:00:00.330Z\"\r\n" +
                "id34,26.6128172874,,,169,\"2015-03-12T00:00:00.340Z\"\r\n" +
                "id35,-224.0770263672,599.0283203125,207,-340,\"2015-03-12T00:00:00.350Z\"\r\n" +
                "id36,0.0175354201,832.0000000000,,32,\"2015-03-12T00:00:00.360Z\"\r\n" +
                "id37,3.9599943161,-69.9140625000,-497,-220,\"2015-03-12T00:00:00.370Z\"\r\n" +
                "id38,-200.0000000000,-568.0000000000,-434,76,\"2015-03-12T00:00:00.380Z\"\r\n" +
                "id39,708.4023437500,0.0000000019,,-478,\"2015-03-12T00:00:00.390Z\"\r\n" +
                "id40,585.2304687500,736.0000000000,140,-237,\"2015-03-12T00:00:00.400Z\"\r\n" +
                "id41,0.0000000141,0.0046403678,-126,-91,\"2015-03-12T00:00:00.410Z\"\r\n" +
                "id42,15.7132420540,-1018.5878906250,389,399,\"2015-03-12T00:00:00.420Z\"\r\n" +
                "id43,386.8437500000,296.6113433838,,-4,\"2015-03-12T00:00:00.430Z\"\r\n" +
                "id44,5.5901532173,-640.0000000000,,-494,\"2015-03-12T00:00:00.440Z\"\r\n" +
                "id45,0.0000002490,0.0000024573,,321,\"2015-03-12T00:00:00.450Z\"\r\n" +
                "id46,0.0000021166,488.0937500000,-10,387,\"2015-03-12T00:00:00.460Z\"\r\n" +
                "id47,0.0000000154,0.0256510917,342,-229,\"2015-03-12T00:00:00.470Z\"\r\n" +
                "id48,0.0000430228,0.6112449020,-26,-472,\"2015-03-12T00:00:00.480Z\"\r\n" +
                "id49,0.0000124788,,343,147,\"2015-03-12T00:00:00.490Z\"\r\n" +
                "id50,0.0454099961,1.4611321092,-236,-164,\"2015-03-12T00:00:00.500Z\"\r\n" +
                "id51,0.0050167134,77.8403434753,-498,-177,\"2015-03-12T00:00:00.510Z\"\r\n" +
                "id52,-416.0000000000,0.0000363594,-478,-425,\"2015-03-12T00:00:00.520Z\"\r\n" +
                "id53,0.0046469892,281.7617187500,361,-201,\"2015-03-12T00:00:00.530Z\"\r\n" +
                "id54,-33.7500000000,-690.2343750000,-356,64,\"2015-03-12T00:00:00.540Z\"\r\n" +
                "id55,0.0000000000,0.0000001103,,-380,\"2015-03-12T00:00:00.550Z\"\r\n" +
                "id56,8.0390167236,-857.6875000000,499,-275,\"2015-03-12T00:00:00.560Z\"\r\n" +
                "id57,21.3488283157,-448.0000000000,120,459,\"2015-03-12T00:00:00.570Z\"\r\n" +
                "id58,0.0000000025,,,49,\"2015-03-12T00:00:00.580Z\"\r\n" +
                "id59,300.0937500000,,-98,-30,\"2015-03-12T00:00:00.590Z\"\r\n" +
                "id60,501.0677947998,0.0030514880,432,-306,\"2015-03-12T00:00:00.600Z\"\r\n" +
                "id61,0.1273584887,,-244,-311,\"2015-03-12T00:00:00.610Z\"\r\n" +
                "id62,740.9882812500,0.0000000720,468,14,\"2015-03-12T00:00:00.620Z\"\r\n" +
                "id63,0.0406596940,0.0000005046,223,403,\"2015-03-12T00:00:00.630Z\"\r\n" +
                "id64,0.0000491961,-272.0000000000,-281,80,\"2015-03-12T00:00:00.640Z\"\r\n" +
                "id65,0.3140142038,512.0000000000,-225,-148,\"2015-03-12T00:00:00.650Z\"\r\n" +
                "id66,0.0055695465,0.0273376359,195,-193,\"2015-03-12T00:00:00.660Z\"\r\n" +
                "id67,-512.0000000000,-844.0000000000,-135,307,\"2015-03-12T00:00:00.670Z\"\r\n" +
                "id68,428.0000000000,-608.0000000000,-76,415,\"2015-03-12T00:00:00.680Z\"\r\n" +
                "id69,-512.0000000000,894.7187500000,402,-362,\"2015-03-12T00:00:00.690Z\"\r\n" +
                "id70,-283.1583251953,832.0000000000,367,-372,\"2015-03-12T00:00:00.700Z\"\r\n" +
                "id71,1.1517004967,97.2561187744,-76,-457,\"2015-03-12T00:00:00.710Z\"\r\n" +
                "id72,768.0000000000,768.0000000000,403,399,\"2015-03-12T00:00:00.720Z\"\r\n" +
                "id73,32.0000000000,174.4927864075,448,-158,\"2015-03-12T00:00:00.730Z\"\r\n" +
                "id74,934.1573791504,4.1113085747,380,320,\"2015-03-12T00:00:00.740Z\"\r\n" +
                "id75,-228.0015869141,0.0066470259,132,105,\"2015-03-12T00:00:00.750Z\"\r\n" +
                "id76,0.0021495742,0.0000005019,-91,-31,\"2015-03-12T00:00:00.760Z\"\r\n" +
                "id77,19.7567439079,0.0000903588,412,-435,\"2015-03-12T00:00:00.770Z\"\r\n" +
                "id78,-1018.3125000000,0.0000000076,-371,60,\"2015-03-12T00:00:00.780Z\"\r\n" +
                "id79,-998.3625488281,380.4352569580,430,150,\"2015-03-12T00:00:00.790Z\"\r\n" +
                "id80,0.0000762123,-864.8203125000,-327,-106,\"2015-03-12T00:00:00.800Z\"\r\n" +
                "id81,288.0000000000,-64.0000000000,-170,385,\"2015-03-12T00:00:00.810Z\"\r\n" +
                "id82,6.0128862858,-498.3359375000,405,-87,\"2015-03-12T00:00:00.820Z\"\r\n" +
                "id83,64.0000000000,,414,-99,\"2015-03-12T00:00:00.830Z\"\r\n" +
                "id84,18.8347930908,-768.0000000000,271,-29,\"2015-03-12T00:00:00.840Z\"\r\n" +
                "id85,0.0025632520,640.0000000000,187,-300,\"2015-03-12T00:00:00.850Z\"\r\n" +
                "id86,0.0000000072,768.0000000000,-161,165,\"2015-03-12T00:00:00.860Z\"\r\n" +
                "id87,0.0000997469,74.7576007843,-139,25,\"2015-03-12T00:00:00.870Z\"\r\n" +
                "id88,8.9508481026,15.2402491570,270,331,\"2015-03-12T00:00:00.880Z\"\r\n" +
                "id89,0.0407589767,0.0025454164,-15,18,\"2015-03-12T00:00:00.890Z\"\r\n" +
                "id90,-642.0000000000,0.0191786536,-164,57,\"2015-03-12T00:00:00.900Z\"\r\n" +
                "id91,391.0000000000,0.0000766759,-308,94,\"2015-03-12T00:00:00.910Z\"\r\n" +
                "id92,0.0000116505,-220.9241943359,-207,-221,\"2015-03-12T00:00:00.920Z\"\r\n" +
                "id93,0.0000000039,-486.9859619141,-479,393,\"2015-03-12T00:00:00.930Z\"\r\n" +
                "id94,0.0000383980,0.3997064978,,-338,\"2015-03-12T00:00:00.940Z\"\r\n" +
                "id95,-628.5000000000,,352,-462,\"2015-03-12T00:00:00.950Z\"\r\n" +
                "id96,-125.0000000000,849.2207031250,490,132,\"2015-03-12T00:00:00.960Z\"\r\n" +
                "id97,-162.5000000000,815.0000000000,-297,-414,\"2015-03-12T00:00:00.970Z\"\r\n" +
                "id98,0.0001471960,0.2934677303,-284,-357,\"2015-03-12T00:00:00.980Z\"\r\n" +
                "id99,-384.0000000000,0.0000000980,-264,355,\"2015-03-12T00:00:00.990Z\"\r\n";
        try {
            channel.reset();
            try (IOContext context = new IOContext(channel, new ServerConfiguration(), MilliClock.INSTANCE)) {
                context.request.read();
                handler.handle(context);
                TestUtils.assertEquals(expected, channel.getOutput());
            }
        } finally {
            channel.free();
        }
    }

    @Test
    public void testQueryHandlerConsistency() throws Exception {
        testHandler(new QueryHandler(new JournalFactoryPool(factory.getConfiguration(), 1), new ServerConfiguration()));
    }

    private void testHandler(ContextHandler handler) throws Exception {
        TestChannel channel = new TestChannel(QUERY1);
        String expected = null;
        handler.setupThread();
        try {
            ServerConfiguration configuration = new ServerConfiguration();
            for (int i = 128; i < 7500; i++) {
                channel.reset();
                configuration.setHttpBufRespContent(i);
                try (IOContext context = new IOContext(channel, configuration, MilliClock.INSTANCE)) {
                    context.request.read();
                    handler.handle(context);
                    if (expected != null) {
                        TestUtils.assertEquals(expected, channel.getOutput());
                    } else {
                        expected = channel.getOutput().toString();
                    }
                }
            }
        } finally {
            channel.free();
        }
        System.out.println(expected);
    }
}
