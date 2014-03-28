package com.example.mytest.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {
    @InjectView(R.id.tv_test)
    TextView tv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        List<City> list = new ArrayList();
        try {
            list = parse(getMyXML());
            for (City city : list) {
                Log.e("tag", city.getName());
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        InputStream myXML = getMyXML();
        Toast.makeText(this, (myXML == null) + "", Toast.LENGTH_SHORT).show();

    }

    private InputStream getMyXML() {
        InputStream open = null;
        try {
            open = getAssets().open("test.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return open;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
//        Test  t = new Test();
    }

    public List<City> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<City> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<City> list = new ArrayList<City>();
        while (parser.next() != XmlPullParser.END_TAG && !String.valueOf(parser.getName()).equals("cities")) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if ("city".equals(name)) {
                list.add(parseCity(parser));
            }
        }
        return list;
    }

    private City parseCity(XmlPullParser parser) throws IOException, XmlPullParserException {
        City city = new City();
        while (!(parser.next() == XmlPullParser.END_TAG && String.valueOf(parser.getName()).equals("city"))) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String parserName = parser.getName();
            if (parserName.equals("name")) {
                parser.next();
                city.setName(parser.getText());
            } else {
                skip(parser);
            }
        }
        return city;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
