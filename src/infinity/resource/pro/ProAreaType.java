// Near Infinity - An Infinity Engine Browser and Editor
// Copyright (C) 2001 - 2005 Jon Olav Hauglid
// See LICENSE.txt for license information

package infinity.resource.pro;

import infinity.datatype.ColorValue;
import infinity.datatype.DecNumber;
import infinity.datatype.Flag;
import infinity.datatype.HashBitmap;
import infinity.datatype.IdsBitmap;
import infinity.datatype.ProRef;
import infinity.datatype.ResourceRef;
import infinity.datatype.Unknown;
import infinity.datatype.UnsignDecNumber;
import infinity.resource.AbstractStruct;
import infinity.resource.AddRemovable;
import infinity.resource.Profile;
import infinity.util.LongIntegerHashMap;

public final class ProAreaType extends AbstractStruct implements AddRemovable
{
  public static final LongIntegerHashMap<String> m_proj = new LongIntegerHashMap<String>();
  public static final String[] s_areaflags = {"Trap not visible", "Trap visible", "Triggered by inanimates",
                                              "Triggered by condition", "Delayed trigger", "Secondary projectile",
                                              "Fragments", "Not affecting allies", "Not affecting enemies",
                                              "Mage-level duration", "Cleric-level duration", "Draw animation",
                                              "Cone-shaped", "Ignore visibility", "Delayed explosion",
                                              "Skip first condition", "Single target"};
  public static final String[] s_areaflagsEx = {
    "No flags set", "Paletted ring", "Random speed", "Start scattered", "Paletted center",
    "Repeat scattering", "Paletted animation", "", "", "", "Oriented fireball puffs",
    "Use hit dice lookup", "", "", "Blend are/ring anim", "Glow area/ring anim", "Hit point limit",
  };

  static {
    m_proj.put(0L, "Fireball");
    m_proj.put(1L, "Stinking cloud");
    m_proj.put(2L, "Cloudkill");
    m_proj.put(3L, "Ice storm");
    m_proj.put(4L, "Grease");
    m_proj.put(5L, "Web");
    m_proj.put(6L, "Meteor");
    m_proj.put(7L, "Horrid wilting");
    m_proj.put(8L, "Teleport field");
    m_proj.put(9L, "Entangle");
    m_proj.put(10L, "Color spray");
    m_proj.put(11L, "Cone of cold");
    m_proj.put(12L, "Holy smite");
    m_proj.put(13L, "Unholy blight");
    m_proj.put(14L, "Prismatic spray");
    m_proj.put(15L, "Red dragon blast");
    m_proj.put(16L, "Storm of vengeance");
    m_proj.put(17L, "Purple fireball");
    m_proj.put(18L, "Green dragon blast");
    m_proj.put(254L, "Custom");
    m_proj.put(255L, "None");
  }


  public ProAreaType() throws Exception
  {
    super(null, "Area effect info", new byte[256], 0);
    setOffset(512);
  }

  public ProAreaType(AbstractStruct superStruct, byte[] buffer, int offset) throws Exception
  {
    super(superStruct, "Area effect info", buffer, offset);
  }

//--------------------- Begin Interface AddRemovable ---------------------

  @Override
  public boolean canRemove()
  {
    return false;   // can not be removed manually
  }

//--------------------- End Interface AddRemovable ---------------------

  @Override
  public int read(byte[] buffer, int offset) throws Exception
  {
    final String[] s_types = Profile.isEnhancedEdition() ? new String[]{"VVC", "BAM"}
                                                         : new String[]{"VEF", "VVC", "BAM"};

    addField(new Flag(buffer, offset, 2, "Area flags", s_areaflags));
    addField(new DecNumber(buffer, offset + 2, 2, "Ray count"));
    addField(new DecNumber(buffer, offset + 4, 2, "Trap size"));
    addField(new DecNumber(buffer, offset + 6, 2, "Explosion size"));
    addField(new ResourceRef(buffer, offset + 8, "Explosion sound", "WAV"));
    addField(new DecNumber(buffer, offset + 16, 2, "Explosion frequency (frames)"));
    addField(new IdsBitmap(buffer, offset + 18, 2, "Fragment animation", "ANIMATE.IDS"));
    addField(new ProRef(buffer, offset + 20, "Secondary projectile", false));
    addField(new DecNumber(buffer, offset + 22, 1, "# repetitions"));
    addField(new HashBitmap(buffer, offset + 23, 1, "Explosion effect", m_proj));
    addField(new ColorValue(buffer, offset + 24, 1, "Explosion color"));
    addField(new Unknown(buffer, offset + 25, 1, "Unused"));
    addField(new ProRef(buffer, offset + 26, "Explosion projectile"));
    addField(new ResourceRef(buffer, offset + 28, "Explosion animation", s_types));
    addField(new DecNumber(buffer, offset + 36, 2, "Cone width"));
    if (Profile.isEnhancedEdition()) {
      addField(new Unknown(buffer, offset + 38, 2));
      addField(new ResourceRef(buffer, offset + 40, "Spread animation", s_types));
      addField(new ResourceRef(buffer, offset + 48, "Ring animation", s_types));
      addField(new ResourceRef(buffer, offset + 56, "Area sound", "WAV"));
      addField(new Flag(buffer, offset + 64, 4, "Extended flags", s_areaflagsEx));
      addField(new UnsignDecNumber(buffer, offset + 68, 2, "# dice for multiple targets"));
      addField(new UnsignDecNumber(buffer, offset + 70, 2, "Dice size for multiple targets"));
      addField(new DecNumber(buffer, offset + 72, 2, "Animation granularity"));
      addField(new DecNumber(buffer, offset + 74, 2, "Animation granularity divider"));
      addField(new Unknown(buffer, offset + 76, 180));
    } else {
      addField(new Unknown(buffer, offset + 38, 218));
    }

    return offset + 256;
  }
}
