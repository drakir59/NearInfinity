// Near Infinity - An Infinity Engine Browser and Editor
// Copyright (C) 2001 - 2005 Jon Olav Hauglid
// See LICENSE.txt for license information

package infinity.datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import infinity.resource.ResourceFactory;
import infinity.resource.StructEntry;
import infinity.resource.key.ResourceEntry;
import infinity.util.IdsMapCache;
import infinity.util.IdsMapEntry;
import infinity.util.LongIntegerHashMap;

public final class ProRef extends ResourceBitmap
{
  private static final ArrayList<RefEntry> proMissileList = new ArrayList<RefEntry>();
  private static final ArrayList<RefEntry> proList = new ArrayList<RefEntry>();

  public ProRef(byte[] buffer, int offset, String name)
  {
    this(null, buffer, offset, 2, name, true);
  }

  public ProRef(byte[] buffer, int offset, String name, boolean useMissile)
  {
    this(null, buffer, offset, 2, name, useMissile);
  }

  public ProRef(StructEntry parent, byte[] buffer, int offset, String name)
  {
    this(parent, buffer, offset, 2, name, true);
  }

  public ProRef(StructEntry parent, byte[] buffer, int offset, String name, boolean useMissile)
  {
    this(parent, buffer, offset, 2, name, useMissile);
  }

  public ProRef(byte[] buffer, int offset, int size, String name)
  {
    this(null, buffer, offset, size, name, true);
  }

  public ProRef(StructEntry parent, byte[] buffer, int offset, int size, String name, boolean useMissile)
  {
    super(parent, buffer, offset, size, name, createRefList(useMissile), null, useMissile ? FMT_NAME_REF_VALUE : FMT_REF_VALUE);
  }

  public ResourceEntry getSelectedEntry()
  {
    return ResourceFactory.getResourceEntry(getResourceName(), true);
  }

  /** Called whenever a new game is opened. */
  public static void clearCache()
  {
    proMissileList.clear();
    proList.clear();
  }

  private static List<RefEntry> createRefList(boolean useMissile)
  {
    if (useMissile) {
      return createProMissileRefList();
    } else {
      return createProRefList();
    }
  }

  private static List<RefEntry> createProMissileRefList()
  {
    if (proMissileList.isEmpty()) {
      // preparing cached list
      LongIntegerHashMap<IdsMapEntry> mslMap = IdsMapCache.get("MISSILE.IDS").getMap();
      LongIntegerHashMap<IdsMapEntry> proMap = IdsMapCache.get("PROJECTL.IDS").getMap();
      int maxSize = Math.max(mslMap.size(), proMap.size());
      proMissileList.ensureCapacity(2 + maxSize);

      if (!mslMap.containsKey(Long.valueOf(0L))) {
        proMissileList.add(new RefEntry(0L, "None", "Default"));
      }
      if (!mslMap.containsKey(Long.valueOf(1L))) {
        proMissileList.add(new RefEntry(1L, "None", "None"));
      }

      long[] keys = mslMap.keys();
      for (final long key: keys) {
        IdsMapEntry mslEntry = mslMap.get(Long.valueOf(key));
        IdsMapEntry proEntry = proMap.get(Long.valueOf(key - 1L));
        RefEntry entry = null;
        if (proEntry != null) {
          entry = new RefEntry(key, proEntry.getString().toUpperCase(Locale.ENGLISH) + ".PRO",
                               mslEntry.getString());
        } else {
          entry = new RefEntry(key, "None", mslEntry.getString());
        }
        proMissileList.add(entry);
      }
    }
    return proMissileList;
  }

  private static List<RefEntry> createProRefList()
  {
    if (proList.isEmpty()) {
      LongIntegerHashMap<IdsMapEntry> proMap = IdsMapCache.get("PROJECTL.IDS").getMap();
      proList.ensureCapacity(2 + proMap.size());

      if (!proMap.containsKey(Long.valueOf(0L))) {
        proList.add(new RefEntry(0L, "None", "None"));
      }

      long[] keys = proMap.keys();
      for (final long key: keys) {
        IdsMapEntry proEntry = proMap.get(Long.valueOf(key));
        proList.add(new RefEntry(key, proEntry.getString().toUpperCase(Locale.ENGLISH) + ".PRO"));
      }
    }
    return proList;
  }
}

