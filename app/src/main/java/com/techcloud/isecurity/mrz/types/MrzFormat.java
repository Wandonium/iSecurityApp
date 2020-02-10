package com.techcloud.isecurity.mrz.types;


import com.techcloud.isecurity.mrz.MrzParseException;
import com.techcloud.isecurity.mrz.MrzRange;
import com.techcloud.isecurity.mrz.MrzRecord;
import com.techcloud.isecurity.mrz.records.MRP;
import com.techcloud.isecurity.mrz.records.MrtdTd1;
import com.techcloud.isecurity.mrz.records.MrtdTd2;

import java.util.ArrayList;

/**
 * Lists all supported MRZ formats. Note that the order of the enum constants are important.
 * @author Martin Vysny, Pierrick Martin
 */
public enum MrzFormat {

    /**
     * MRTD td1 format: A three line long, 30 characters per line format.
     */
    MRTD_TD1(3, 30, MrtdTd1.class),
    /**
     * MRTD td2 format: A two line long, 36 characters per line format.
     */
    MRTD_TD2(2, 36, MrtdTd2.class),
    /**
     * MRP Passport format: A two line long, 44 characters per line format.
     */
    PASSPORT(2, 44, MRP.class);

    public final int rows;
    public final int columns;
    private final Class<? extends MrzRecord> recordClass;

    private MrzFormat(int rows, int columns, Class<? extends MrzRecord> recordClass) {
        this.rows = rows;
        this.columns = columns;
        this.recordClass = recordClass;
    }

    /**
     * Checks if this format is able to parse given serialized MRZ record.
     * @param mrzRows MRZ record, separated into rows.
     * @return true if given MRZ record is of this type, false otherwise.
     */
    public boolean isFormatOf(String[] mrzRows) {
        return rows == mrzRows.length && columns == mrzRows[0].length();
    }

    /**
     * Detects given MRZ format.
     * @param mrz the MRZ string.
     * @return the format, never null.
     */
    public static final MrzFormat get(String mrz) {
        final String[] rows = mrz.split("\\r?\\n");
        final int cols = rows[0].length();
        for (int i = 1; i < rows.length; i++) {
            if (rows[i].length() != cols) {
                throw new MrzParseException("Different row lengths: 0: " + cols + " and " + i + ": " + rows[i].length(), mrz, new MrzRange(0, 0, 0), null);
            }
        }
        for (final MrzFormat f : values()) {
            if (f.isFormatOf(rows)) {
                return f;
            }
        }
        throw new MrzParseException("Unknown format / unsupported number of cols/rows: " + cols + "/" + rows.length, mrz, new MrzRange(0, 0, 0), null);
    }

    /**
     * Creates new record instance with this type.
     * @return never null record instance.
     */
    public final MrzRecord newRecord() {
        try {
            return recordClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
