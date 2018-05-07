package pzy64.PzyCrypt.Pro;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class About extends AppCompatActivity {
    Button abt, rate;
    TextView licen;

    String licence = "Copyright (C) 1999-2013,2014,2015,2016 Jack Lloyd\n" +
            "              2001 Peter J Jones\n" +
            "              2004-2007 Justin Karneges\n" +
            "              2004 Vaclav Ovsik\n" +
            "              2005 Matthew Gregan\n" +
            "              2005-2006 Matt Johnston\n" +
            "              2006 Luca Piccarreta\n" +
            "              2007 Yves Jerschow\n" +
            "              2007,2008 FlexSecure GmbH\n" +
            "              2007,2008 Technische Universitat Darmstadt\n" +
            "              2007,2008,2010,2014 Falko Strenzke\n" +
            "              2007,2008 Martin Doering\n" +
            "              2007 Manuel Hartl\n" +
            "              2007 Christoph Ludwig\n" +
            "              2007 Patrick Sona\n" +
            "              2008 Copyright Projet SECRET, INRIA, Rocquencourt\n" +
            "              2008 Bhaskar Biswas and Nicolas Sendrier\n" +
            "              2008 Google Inc.\n" +
            "              2010 Olivier de Gaalon\n" +
            "              2012 Vojtech Kral\n" +
            "              2012,2014 Markus Wanner\n" +
            "              2013 Joel Low\n" +
            "              2014 cryptosource GmbH\n" +
            "              2014 Andrew Moon\n" +
            "              2015 Daniel Seither (Kullo GmbH)\n" +
            "              2015 Simon Warta (Kullo GmbH)\n" +
            "              2015 Matej Kenda (TopIT d.o.o.)\n" +
            "              2015 René Korthaus\n" +
            "              2015,2016 Daniel Neus\n" +
            "              2015 Uri Blumenthal\n" +
            "              2015,2016 Kai Michaelis\n" +
            "All rights reserved.\n" +
            "\n" +
            "Redistribution and use in source and binary forms, with or without\n" +
            "modification, are permitted provided that the following conditions are met:\n" +
            "\n" +
            "1. Redistributions of source code must retain the above copyright notice,\n" +
            "   this list of conditions, and the following disclaimer.\n" +
            "\n" +
            "2. Redistributions in binary form must reproduce the above copyright\n" +
            "   notice, this list of conditions, and the following disclaimer in the\n" +
            "   documentation and/or other materials provided with the distribution.\n" +
            "\n" +
            "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"\n" +
            "AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE\n" +
            "IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE\n" +
            "ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE\n" +
            "LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR\n" +
            "CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF\n" +
            "SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS\n" +
            "INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN\n" +
            "CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)\n" +
            "ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE\n" +
            "POSSIBILITY OF SUCH DAMAGE.\n";

    int DIAL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        abt = (Button) findViewById(R.id.contact);
        rate = (Button) findViewById(R.id.rate);
        licen = (TextView) findViewById(R.id.botan);
        abt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://play.google.com/store/apps/developer?id=Pzy64";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        licen.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                showDialog(3);
            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://play.google.com/store/apps/details?id=pzy64.PzyCrypt.Pro";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog d = new Dialog(About.this);
        d.setContentView(R.layout.dialog_ui);
        d.setTitle("LICENCE");
        TextView tv = (TextView) d.findViewById(R.id.licence);
        tv.setText(licence);
        return d;
    }
}
