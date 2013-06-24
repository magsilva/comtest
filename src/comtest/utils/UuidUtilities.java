// ComTest - Comments for testing
package comtest.utils;

import java.util.Collection;
import java.util.UUID;

/**
 * Utilities for GUID/UUID manipulation
 * @author tojukarp
 */
public abstract class UuidUtilities {
    // #import java.util.ArrayList;
    // #import java.util.List;
    // #import java.util.UUID;
    
    /**
     * Converts UUID into a string
     * @param uuid UUID to convert
     * @param braces Surround with curly braces {}
     * @return Upper-case UUID string, null if null uuid
     * @example
     * <pre name="test">
     * UUID u = UUID.fromString("3AC096D0-A1C2-E12C-1390-A8335801FDAB");
     * uuidToStr(u, true) === "{3AC096D0-A1C2-E12C-1390-A8335801FDAB}";
     * uuidToStr(u, false) === "3AC096D0-A1C2-E12C-1390-A8335801FDAB";
     * </pre>
     */
    public static String uuidToStr(UUID uuid, boolean braces) {
        if ( uuid == null )
            return null;

        String uuidString = uuid.toString().toUpperCase();
        return braces ? String.format("{%s}", uuidString) : uuidString;
    }

    /**
     * Converts string into an UUID
     * @param uuidString UUID string to convert
     * @return UUID, null if invalid format
     * @example
     * <pre name="test">
     * String uustr = "FAE04EC0-301F-11D3-BF4B-00C04F79EFBC";
     * UUID u1 = strToUuid(uustr);
     * UUID u2 = strToUuid("{" + uustr + "}");
     * u1.toString().toUpperCase() === uustr;
     * u2.toString().toUpperCase() === uustr;
     * </pre>
     */
    public static UUID strToUuid(String uuidString) {
        if ( uuidString == null )
            return null;

        try {
            if ( uuidString.length() == 38 )
                return UUID.fromString(uuidString.substring(1, 37));
            else if ( uuidString.length() == 36 )
                return UUID.fromString(uuidString);
        } catch ( IllegalArgumentException iae ) {
            //return null;
        }

        return null;
    }

    /**
     * Add UUIDs from one collection to another, calling strToUuid for each
     * item and ignoring invalid or null entries.
     * @param dest Collection to add to (UUIDs)
     * @param source Collection to add from (strings)
     * @example
     * <pre name="test">
     * List<UUID> dest = new ArrayList<UUID>();
     * dest.add(strToUuid("ca8a8f12-b28a-4434-a316-ff2e1ce9ea58"));
     * List<String> src = new ArrayList<String>();
     * src.add("2d2d6946-1561-49aa-ba2d-55f60bb9e517");
     * src.add("6272bfdd-21e7-4a8e-bd0e-a94531399838");
     * src.add("7c122081-5374-4aeb-9e6b-7c40574de672");
     * addUuids(dest, src);
     * src.size() === 3;
     * dest.size() === 4;
     * uuidToStr(dest.get(0), false) === "CA8A8F12-B28A-4434-A316-FF2E1CE9EA58";
     * uuidToStr(dest.get(1), false) === "2D2D6946-1561-49AA-BA2D-55F60BB9E517";
     * uuidToStr(dest.get(2), false) === "6272BFDD-21E7-4A8E-BD0E-A94531399838";
     * uuidToStr(dest.get(3), false) === "7C122081-5374-4AEB-9E6B-7C40574DE672";
     * </pre>
     */
    public static void addUuids(Collection<UUID> dest, Collection<String> source) {
        if ( source == null || dest == null )
            return;

        for ( String strUuid : source ) {
            UUID uuid = strToUuid(strUuid);
            if ( uuid != null )
                dest.add(uuid);
        }
    }

    /**
     * Add UUIDs from one collection to another, calling uuidToStr for each
     * item and ignoring invalid or null entries.
     * @param dest Collection to add to (stringss)
     * @param source Collection to add from (UUIDs)
     * @param braces Surround with curly braces {}
     * @example
     * <pre name="test">
     * List<String> dest = new ArrayList<String>();
     * dest.add("CA8A8F12-B28A-4434-A316-FF2E1CE9EA58");
     * List<UUID> src = new ArrayList<UUID>();
     * src.add(strToUuid("2d2d6946-1561-49aa-ba2d-55f60bb9e517"));
     * src.add(strToUuid("6272bfdd-21e7-4a8e-bd0e-a94531399838"));
     * src.add(strToUuid("7c122081-5374-4aeb-9e6b-7c40574de672"));
     * addUuids(dest, src, false);
     * src.size() === 3;
     * dest.size() === 4;
     * dest.get(0) === "CA8A8F12-B28A-4434-A316-FF2E1CE9EA58";
     * dest.get(1) === "2D2D6946-1561-49AA-BA2D-55F60BB9E517";
     * dest.get(2) === "6272BFDD-21E7-4A8E-BD0E-A94531399838";
     * dest.get(3) === "7C122081-5374-4AEB-9E6B-7C40574DE672";
     * </pre>
     */
    public static void addUuids(Collection<String> dest, Collection<UUID> source, boolean braces) {
        if ( source == null || dest == null )
            return;
        
        for ( UUID uuid : source ) {
            String strUuid = uuidToStr(uuid, braces);
            if ( strUuid != null )
                dest.add(strUuid);
        }
    }
}
