/*
   (с) 2016 ООО "КУРС-ИТ"

   Этот файл — часть КУРС:Xylophone.

   КУРС:Xylophone — свободная программа: вы можете перераспространять ее и/или изменять
   ее на условиях Стандартной общественной лицензии ограниченного применения GNU (LGPL)
   в том виде, в каком она была опубликована Фондом свободного программного обеспечения; либо
   версии 3 лицензии, либо (по вашему выбору) любой более поздней версии.

   Эта программа распространяется в надежде, что она будет полезной,
   но БЕЗО ВСЯКИХ ГАРАНТИЙ; даже без неявной гарантии ТОВАРНОГО ВИДА
   или ПРИГОДНОСТИ ДЛЯ ОПРЕДЕЛЕННЫХ ЦЕЛЕЙ. Подробнее см. в Стандартной
   общественной лицензии GNU.

   Вы должны были получить копию Стандартной общественной лицензии  ограниченного
   применения GNU (LGPL) вместе с этой программой. Если это не так,
   см. http://www.gnu.org/licenses/.


   Copyright 2016, COURSE-IT Ltd.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public License
   along with this program.  If not, see http://www.gnu.org/licenses/.

*/
package ru.curs.xylophone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Класс для запуска из командной строки.
 *
 */
public class Main {
    private static final String DATA = "-data";
    private static final String TEMPLATE = "-template";
    private static final String DESCR = "-descr";
    private static final String OUT = "-out";
    private static final String SAX = "-sax";
    private static final String COPYTEMPLATE = "-copytemplate";

    private enum State {
        READTOKEN, READDATA, READTEMPLATE, READDESCR, READOUT,
    }

    /**
     * Главный метод класса.
     *
     * @param args
     *            аргументы
     * @throws XML2SpreadSheetError
     *             в случае, если произошла ошибка конвертации
     * @throws FileNotFoundException
     *             в случае, если файл не найден
     */
    public static void main(String[] args) throws FileNotFoundException,
            XML2SpreadSheetError {

        FileInputStream iff = null;
        File descr = null;
        File template = null;
        FileOutputStream output = null;
        boolean useSAX = false;
        boolean copyTemplate = false;

        State state = State.READTOKEN;

        for (String s : args)
            switch (state) {
            case READTOKEN:
                if (DATA.equalsIgnoreCase(s))
                    state = State.READDATA;
                else if (TEMPLATE.equalsIgnoreCase(s))
                    state = State.READTEMPLATE;
                else if (DESCR.equalsIgnoreCase(s))
                    state = State.READDESCR;
                else if (OUT.equalsIgnoreCase(s))
                    state = State.READOUT;
                else if (SAX.equalsIgnoreCase(s))
                    useSAX = true;
                else if (COPYTEMPLATE.equalsIgnoreCase(s))
                    copyTemplate = true;
                else
                    showHelp();
                break;
            case READDATA:
                iff = new FileInputStream(new File(s));
                state = State.READTOKEN;
                break;
            case READTEMPLATE:
                template = new File(s);
                state = State.READTOKEN;
                break;
            case READDESCR:
                descr = new File(s);
                state = State.READTOKEN;
                break;
            case READOUT:
                output = new FileOutputStream(new File(s));
                state = State.READTOKEN;
                break;
            default:
                break;
            }

        checkParams(iff, descr, template, output);
        XML2Spreadsheet.process(iff, descr, template, useSAX, copyTemplate,
                output);

        System.out.println("Spreadsheet created successfully.");
    }

    private static void checkParams(FileInputStream iff, File descr,
            File template, FileOutputStream output) {
        if (iff == null || descr == null || template == null || output == null)
            showHelp();
    }

    private static void showHelp() {
        System.out
                .println("Xylophone should be called with the following parameters (any order):");
        System.out.println(DATA + " XML data file");
        System.out.println(TEMPLATE + " XLS/XLSX template file");
        System.out.println(DESCR + " descriptor file");
        System.out.println("[" + SAX
                + "] use SAX engine (instead of DOM) to parse data file");
        System.out.println("[" + COPYTEMPLATE
                + "] copy the template file to output before processing");
        System.out.println(OUT + " output file");

        System.exit(1);
    }
}
