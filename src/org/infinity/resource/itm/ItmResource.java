// Near Infinity - An Infinity Engine Browser and Editor
// Copyright (C) 2001 - 2005 Jon Olav Hauglid
// See LICENSE.txt for license information

package org.infinity.resource.itm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.infinity.datatype.Bitmap;
import org.infinity.datatype.DecNumber;
import org.infinity.datatype.Flag;
import org.infinity.datatype.IdsBitmap;
import org.infinity.datatype.ResourceRef;
import org.infinity.datatype.SectionCount;
import org.infinity.datatype.SectionOffset;
import org.infinity.datatype.StringRef;
import org.infinity.datatype.TextBitmap;
import org.infinity.datatype.TextString;
import org.infinity.datatype.Unknown;
import org.infinity.gui.StructViewer;
import org.infinity.gui.hexview.BasicColorMap;
import org.infinity.gui.hexview.StructHexViewer;
import org.infinity.resource.AbstractAbility;
import org.infinity.resource.AbstractStruct;
import org.infinity.resource.AddRemovable;
import org.infinity.resource.Effect;
import org.infinity.resource.HasAddRemovable;
import org.infinity.resource.HasViewerTabs;
import org.infinity.resource.Profile;
import org.infinity.resource.Resource;
import org.infinity.resource.ResourceFactory;
import org.infinity.resource.StructEntry;
import org.infinity.resource.key.ResourceEntry;
import org.infinity.search.SearchOptions;
import org.infinity.util.StringResource;
import org.infinity.util.io.StreamUtils;

public final class ItmResource extends AbstractStruct implements Resource, HasAddRemovable, HasViewerTabs
{
  // ITM-specific field labels
  public static final String ITM_NAME_GENERAL           = "General name";
  public static final String ITM_NAME_IDENTIFIED        = "Identified name";
  public static final String ITM_DROP_SOUND             = "Drop sound";
  public static final String ITM_FLAGS                  = "Flags";
  public static final String ITM_CATEGORY               = "Category";
  public static final String ITM_UNUSABLE_BY            = "Unusable by";
  public static final String ITM_EQUIPPED_APPEARANCE    = "Equipped appearance";
  public static final String ITM_USED_UP_ITEM           = "Used up item";
  public static final String ITM_MIN_LEVEL              = "Minimum level";
  public static final String ITM_MIN_STRENGTH           = "Minimum strength";
  public static final String ITM_MIN_STRENGTH_BONUS     = "Minimum strength bonus";
  public static final String ITM_MIN_INTELLIGENCE       = "Minimum intelligence";
  public static final String ITM_MIN_DEXTERITY          = "Minimum dexterity";
  public static final String ITM_MIN_WISDOM             = "Minimum wisdom";
  public static final String ITM_MIN_CONSTITUTION       = "Minimum constitution";
  public static final String ITM_MIN_CHARISMA           = "Minimum charisma";
  public static final String ITM_UNUSABLE_BY_1          = "Unusable by (1/4)";
  public static final String ITM_UNUSABLE_BY_2          = "Unusable by (2/4)";
  public static final String ITM_UNUSABLE_BY_3          = "Unusable by (3/4)";
  public static final String ITM_UNUSABLE_BY_4          = "Unusable by (4/4)";
  public static final String ITM_WEAPON_PROFICIENCY     = "Weapon proficiency";
  public static final String ITM_PRICE                  = "Price";
  public static final String ITM_MAX_IN_STACK           = "Maximum in stack";
  public static final String ITM_ICON                   = "Icon";
  public static final String ITM_LORE                   = "Lore to identify";
  public static final String ITM_ICON_GROUND            = "Ground icon";
  public static final String ITM_WEIGHT                 = "Weight";
  public static final String ITM_DESCRIPTION_GENERAL    = "General description";
  public static final String ITM_DESCRIPTION_IDENTIFIED = "Identified description";
  public static final String ITM_PICK_UP_SOUND          = "Pick up sound";
  public static final String ITM_DESCRIPTION_IMAGE      = "Description image";
  public static final String ITM_ENCHANTMENT            = "Enchantment";
  public static final String ITM_OFFSET_ABILITIES       = "Abilities offset";
  public static final String ITM_NUM_ABILITIES          = "# abilities";
  public static final String ITM_OFFSET_EFFECTS         = "Effects offset";
  public static final String ITM_FIRST_EFFECT_INDEX     = "First effect index";
  public static final String ITM_NUM_GLOBAL_EFFECTS     = "# global effects";
  public static final String ITM_DIALOG                 = "Dialogue";
  public static final String ITM_SPEAKER_NAME           = "Speaker name";
  public static final String ITM_WEAPON_COLOR           = "Weapon color";

  public static final String[] s_categories =
          {"Miscellaneous", "Amulets and necklaces", "Armor", "Belts and girdles",
           "Boots", "Arrows", "Bracers and gauntlets", "Headgear",
           "Keys", "Potions", "Rings", "Scrolls", "Shields", "Food",
           "Bullets", "Bows", "Daggers", "Maces", "Slings", "Small swords",
           "Large swords", "Hammers", "Morning stars", "Flails", "Darts",
           "Axes", "Quarterstaves", "Crossbows", "Hand-to-hand weapons",
           "Spears", "Halberds", "Bolts", "Cloaks and robes",
           "Gold pieces", "Gems", "Wands", "Containers", "Books",
           "Familiars", "Tattoos", "Lenses", "Bucklers",
           "Candles", "Child bodies", "Clubs", "Female bodies", "Keys (old)",
           "Large shields", "Male bodies", "Medium shields", "Notes",
           "Rods", "Skulls", "Small shields", "Spider bodies",
           "Telescopes", "Bottles", "Greatswords", "Bags",
           "Furs and pelts", "Leather armor", "Studded leather", "Chain mail",
           "Splint mail", "Plate mail", "Full plate", "Hide armor", "Robes",
           "Scale mail", "Bastard swords", "Scarves", "Rations", "Hats", "Gloves"};
  public static final String[] s_categories11 =
          {"Miscellaneous", "Amulets and necklaces", "Armor", "Belts and girdles",
           "Boots", "Arrows", "Bracers and gauntlets", "Headgear",
           "Keys", "Potions", "Rings", "Scrolls", "Shields", "Spells",
           "Bullets", "Bows", "Daggers", "Maces", "Slings", "Small swords",
           "Large swords", "Hammers", "Morning stars", "Flails", "Darts",
           "Axes", "Quarterstaves", "Crossbows", "Hand-to-hand weapons",
           "Greatswords", "Halberds", "Bolts", "Cloaks and robes",
           "Copper commons", "Gems", "Wands", "Eyeballs", "Bracelets",
           "Earrings", "Tattoos", "Lenses", "Teeth"};
  public static final String[] s_flags =
          {"None", "Unsellable", "Two-handed", "Droppable", "Displayable",
           "Cursed", "Not copyable", "Magical", "Left-handed", "Silver", "Cold iron", "Off-handed",
           "Conversable", "EE: Fake two-handed", "EE: Forbid off-hand weapon", "", "", "", "", "",
           "", "", "", "", "", "EE/Ex: Undispellable", "EE/Ex: Toggle critical hits"};
  public static final String[] s_flags11 =
          {"None", "Unsellable", "Two-handed", "Droppable", "Displayable",
           "Cursed", "Not copyable", "Magical", "Left-handed", "Silver", "Cold iron", "Steel", "Conversable",
           "Pulsating"};
  public static final String[] s_usability =
          {"None",
           "Chaotic;Includes Chaotic Good, Chaotic Neutral and Chaotic Evil",
           "Evil;Includes Lawful Evil, Neutral Evil and Chaotic Evil",
            "Good;Includes Lawful Good, Neutral Good and Chaotic Good",
           "... Neutral;Includes Lawful Neutral, True Neutral and Chaotic Neutral",
           "Lawful;Includes Lawful Good, Lawful Neutral and Lawful Evil",
           "Neutral ...;Includes Neutral Good, True Neutral and Neutral Evil",
           "Bard", "Cleric", "Cleric-Mage",
           "Cleric-Thief", "Cleric-Ranger", "Fighter", "Fighter-Druid",
           "Fighter-Mage", "Fighter-Cleric", "Fighter-Mage-Cleric",
           "Fighter-Mage-Thief", "Fighter-Thief", "Mage", "Mage-Thief",
           "Paladin", "Ranger", "Thief", "Elf", "Dwarf", "Half-Elf",
           "Halfling", "Human", "Gnome", "Monk", "Druid", "Half-orc"};
  public static final String[] s_usability11 =
          {"None",
           "Chaotic;Includes Chaotic Good, Chaotic Neutral and Chaotic Evil",
           "Evil;Includes Lawful Evil, Neutral Evil and Chaotic Evil",
           "Good;Includes Lawful Good, Neutral Good and Chaotic Good",
           "... Neutral;Includes Lawful Neutral, True Neutral and Chaotic Neutral",
           "Lawful;Includes Lawful Good, Lawful Neutral and Lawful Evil",
           "Neutral ...;Includes Neutral Good, True Neutral and Neutral Evil",
           "Sensate", "Priest", "Godsman", "Anarchist", "Xaositect",
           "Fighter", "Non-aligned", "Fighter-Mage", "Dustman",
           "Mercykiller", "Indep", "Figher-Thief", "Mage",
           "Mage-Thief", "Dak'kon", "Fall-From-Grace", "Thief",
           "Vhailor", "Ignus", "Morte", "Nordom",
           "Human", "Annah", "", "Nameless One", ""
          };
  public static final String[] s_usability20 =
          {"None", "Barbarian", "Bard", "Cleric", "Druid",
           "Fighter", "Monk", "Paladin", "Ranger",
           "Rogue", "Sorcerer", "Wizard", "",
           "Chaotic;Includes Chaotic Good, Chaotic Neutral and Chaotic Evil",
           "Evil;Includes Lawful Evil, Neutral Evil and Chaotic Evil",
           "Good;Includes Lawful Good, Neutral Good and Chaotic Good",
           "... Neutral;Includes Lawful Neutral, True Neutral and Chaotic Neutral",
           "Lawful;Includes Lawful Good, Lawful Neutral and Lawful Evil",
           "Neutral ...;Includes Neutral Good, True Neutral and Neutral Evil",
           "", "", "", "", "", "Elf",
           "Dwarf", "Half-elf", "Halfling", "Human", "Gnome"
          };
  public static final String[] s_kituse1 =
          {"None", "Cleric of talos", "Cleric of helm", "Cleric of lathander",
           "Totemic druid", "Shapeshifter", "Avenger", "Barbarian", "Wild mage"};
  public static final String[] s_kituse2 =
          {"None", "Stalker", "Beastmaster", "Assassin", "Bounty hunter",
           "Swashbuckler", "Blade", "Jester", "Skald"};
  public static final String[] s_kituse3 =
          {"None", "Diviner", "Enchanter", "Illusionist", "Invoker", "Necromancer", "Transmuter",
           "Generalist", "Archer"};
  public static final String[] s_kituse4 =
          {"None", "Berserker", "Wizard slayer", "Kensai", "Cavalier", "Inquisitor",
           "Undead hunter", "Abjurer", "Conjurer"};
  public static final String[] s_tag = {"  ", "2A", "3A", "4A", "2W", "3W", "4W", "AX", "BW",
                                        "CB", "CL", "D1", "D2", "D3", "D4", "DD", "FL", "FS",
                                        "H0", "H1", "H2", "H3", "H4", "H5", "H6", "HB", "MC",
                                        "MS", "QS", "S1", "S2", "S3", "SC", "SL", "SP", "SS", "WH"};
  public static final String[] s_anim =
          {"None", "Leather armor", "Chain mail", "Plate mail",
           "Mage robe 1", "Mage robe 2", "Mage robe 3",
           "Battle axe", "Bow", "Crossbow", "Club",
           "Buckler", "Small shield", "Medium shield", "Large shield",
           "Dagger", "Flail", "Flaming sword",
           "Helmet 1", "Helmet 2", "Helmet 3", "Helmet 4", "Helmet 5", "Helmet 6", "Helmet 7",
           "Halberd", "Mace", "Morning star", "Quarterstaff",
           "Long sword", "Two-handed sword", "Katana", "Scimitar",
           "Sling", "Spear", "Short sword", "War hammer"
          };
  public static final String[] s_tag11 = {"  ", "AX", "CB", "CL", "DD", "S1", "WH"};
  public static final String[] s_anim11 = {"None", "Axe", "Crossbow", "Club", "Dagger",
                                           "Sword", "Hammer"};
  public static final String[] s_tag_1pp = {"  ", "2A", "3A", "4A", "2W", "3W", "4W", "AX",
                                            "BS", "BW", "C0", "C1", "C2", "C3", "C4", "C5",
                                            "C6", "C7", "CB", "CL", "D1", "D2", "D3", "D4",
                                            "DD", "F0", "F1", "F2", "F3",
                                            "FL", "FS", "GS",
                                            "H0", "H1", "H2", "H3", "H4", "H5", "H6", "HB",
                                            "J0", "J1", "J2", "J3", "J4", "J5", "J6", "J7",
                                            "J8", "J9", "JA", "JB", "JC", "M2", "MC",
                                            "MS", "Q2", "Q3", "Q4",
                                            "QS", "S0", "S1", "S2", "S3", "SC", "SL", "SP",
                                            "SS", "WH", "YW", "ZW"};
  public static final String[] s_anim_1pp =
          {"None", "Leather armor", "Chain mail", "Plate mail",
           "Mage robe 1", "Mage robe 2", "Mage robe 3",
           "Battle axe", "Bow?", "Bow",
           "Small shield (alternate 1)", "Medium shield (alternate 1)", "Large shield (alternate 1)",
           "Medium shield (alternate 2)", "Small shield (alternate 2)", "Large shield (alternate 2)",
           "Large shield (alternate 3)", "Medium shield (alternate 3)",
           "Crossbow", "Club",
           "Buckler", "Small shield", "Medium shield", "Large shield",
           "Dagger",
           "Flail (alternate 1)", "Flail (alternate 2)", "Flaming sword (blue)", "Flail (alternate 3",
           "Flail", "Flaming sword", "Glowing staff",
           "Helmet 1", "Helmet 2", "Helmet 3", "Helmet 4", "Helmet 5", "Helmet 6", "Helmet 7",
           "Halberd",
           "Helmet 8", "Helmet 9", "Helmet 10", "Helmet 11", "Helmet 12", "Helmet 13", "Helmet 14",
           "Helmet 15", "Helmet 16", "Helmet 17", "Helmet 18", "Circlet", "Helmet 20",
           "Mace (alternate)", "Mace", "Morning star",
           "Quarterstaff (alternate 1)", "Quarterstaff (alternate 2)", "Quarterstaff (alternate 3)",
           "Quarterstaff", "Bastard sword", "Long sword", "Two-handed sword", "Katana", "Scimitar",
           "Sling", "Spear", "Short sword", "War hammer", "Wings?", "Feathered wings"
          };

  private StructHexViewer hexViewer;

  public static String getSearchString(InputStream is) throws IOException
  {
    is.skip(8);
    String defName = StringResource.getStringRef(StreamUtils.readInt(is)).trim();
    String name = StringResource.getStringRef(StreamUtils.readInt(is)).trim();
    if (name.isEmpty() || name.equalsIgnoreCase("No such index")) {
      return defName;
    } else {
      return name;
    }
  }

  public ItmResource(ResourceEntry entry) throws Exception
  {
    super(entry);
  }

// --------------------- Begin Interface HasAddRemovable ---------------------

  @Override
  public AddRemovable[] getAddRemovables() throws Exception
  {
    return new AddRemovable[]{new Ability(), new Effect()};
  }

  @Override
  public AddRemovable confirmAddEntry(AddRemovable entry) throws Exception
  {
    return entry;
  }

  @Override
  public boolean confirmRemoveEntry(AddRemovable entry) throws Exception
  {
    return true;
  }

// --------------------- End Interface HasAddRemovable ---------------------


// --------------------- Begin Interface HasViewerTabs ---------------------

  @Override
  public int getViewerTabCount()
  {
    return 2;
  }

  @Override
  public String getViewerTabName(int index)
  {
    switch (index) {
      case 0:
        return StructViewer.TAB_VIEW;
      case 1:
        return StructViewer.TAB_RAW;
    }
    return null;
  }

  @Override
  public JComponent getViewerTab(int index)
  {
    switch (index) {
      case 0:
      {
        JScrollPane scroll = new JScrollPane(new Viewer(this));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
      }
      case 1:
      {
        if (hexViewer == null) {
          hexViewer = new StructHexViewer(this, new BasicColorMap(this, true));
        }
        return hexViewer;
      }
    }
    return null;
  }

  @Override
  public boolean viewerTabAddedBefore(int index)
  {
    return (index == 0);
  }

// --------------------- End Interface HasViewerTabs ---------------------


// --------------------- Begin Interface Writeable ---------------------

  @Override
  public void write(OutputStream os) throws IOException
  {
    super.write(os);
    for (int i = 0; i < getFieldCount(); i++) {
      Object o = getField(i);
      if (o instanceof Ability) {
        Ability a = (Ability)o;
        a.writeEffects(os);
      }
    }
  }

// --------------------- End Interface Writeable ---------------------

  @Override
  protected void viewerInitialized(StructViewer viewer)
  {
    viewer.addTabChangeListener(hexViewer);
  }

  @Override
  protected void datatypeAdded(AddRemovable datatype)
  {
    if (datatype instanceof Effect) {
      for (int i = 0; i < getFieldCount(); i++) {
        Object o = getField(i);
        if (o instanceof Ability)
          ((Ability)o).incEffectsIndex(1);
      }
    }
    else if (datatype instanceof Ability) {
      int effect_count = ((SectionCount)getAttribute(ITM_NUM_GLOBAL_EFFECTS)).getValue();
      for (int i = 0; i < getFieldCount(); i++) {
        Object o = getField(i);
        if (o instanceof Ability) {
          Ability ability = (Ability)o;
          ability.setEffectsIndex(effect_count);
          effect_count += ability.getEffectsCount();
        }
      }
    }
    if (hexViewer != null) {
      hexViewer.dataModified();
    }
  }

  @Override
  protected void datatypeAddedInChild(AbstractStruct child, AddRemovable datatype)
  {
    super.datatypeAddedInChild(child, datatype);
    if (child instanceof Ability && datatype instanceof Effect) {
      int index = getIndexOf(child) + 1;
      while (index < getFieldCount()) {
        StructEntry se = getField(index++);
        if (se instanceof Ability)
          ((Ability)se).incEffectsIndex(1);
      }
    }
    if (hexViewer != null) {
      hexViewer.dataModified();
    }
  }

  @Override
  protected void datatypeRemoved(AddRemovable datatype)
  {
    if (datatype instanceof Effect) {
      for (int i = 0; i < getFieldCount(); i++) {
        Object o = getField(i);
        if (o instanceof Ability)
          ((Ability)o).incEffectsIndex(-1);
      }
    }
    else if (datatype instanceof Ability) {
      int effect_count = ((SectionCount)getAttribute(ITM_NUM_GLOBAL_EFFECTS)).getValue();
      for (int i = 0; i < getFieldCount(); i++) {
        Object o = getField(i);
        if (o instanceof Ability) {
          Ability ability = (Ability)o;
          ability.setEffectsIndex(effect_count);
          effect_count += ability.getEffectsCount();
        }
      }
    }
    if (hexViewer != null) {
      hexViewer.dataModified();
    }
  }

  @Override
  protected void datatypeRemovedInChild(AbstractStruct child, AddRemovable datatype)
  {
    super.datatypeRemovedInChild(child, datatype);
    if (child instanceof Ability && datatype instanceof Effect) {
      int index = getIndexOf(child) + 1;
      while (index < getFieldCount()) {
        StructEntry se = getField(index++);
        if (se instanceof Ability)
          ((Ability)se).incEffectsIndex(-1);
      }
    }
    if (hexViewer != null) {
      hexViewer.dataModified();
    }
  }

  @Override
  public int read(ByteBuffer buffer, int offset) throws Exception
  {
    addField(new TextString(buffer, 0, 4, COMMON_SIGNATURE));
    TextString version = new TextString(buffer, 4, 4, COMMON_VERSION);
    addField(version);
    addField(new StringRef(buffer, 8, ITM_NAME_GENERAL));
    addField(new StringRef(buffer, 12, ITM_NAME_IDENTIFIED));
    if (version.toString().equalsIgnoreCase("V1.1")) {
      addField(new ResourceRef(buffer, 16, ITM_DROP_SOUND, "WAV"));
      addField(new Flag(buffer, 24, 4, ITM_FLAGS, s_flags11));
      addField(new Bitmap(buffer, 28, 2, ITM_CATEGORY, s_categories11));
      addField(new Flag(buffer, 30, 4, ITM_UNUSABLE_BY, s_usability11));
      addField(new TextBitmap(buffer, 34, 2, ITM_EQUIPPED_APPEARANCE, s_tag11, s_anim11));
    }
    else {
      addField(new ResourceRef(buffer, 16, ITM_USED_UP_ITEM, "ITM"));
      addField(new Flag(buffer, 24, 4, ITM_FLAGS, s_flags));
      addField(new Bitmap(buffer, 28, 2, ITM_CATEGORY, s_categories));
      if (version.toString().equalsIgnoreCase("V2.0")) {
        addField(new Flag(buffer, 30, 4, ITM_UNUSABLE_BY, s_usability20));
      } else {
        addField(new Flag(buffer, 30, 4, ITM_UNUSABLE_BY, s_usability));
      }
      if (Profile.isEnhancedEdition()) {
        addField(new TextBitmap(buffer, 34, 2, ITM_EQUIPPED_APPEARANCE, s_tag_1pp, s_anim_1pp));
      }
      else {
        addField(new TextBitmap(buffer, 34, 2, ITM_EQUIPPED_APPEARANCE, s_tag, s_anim));
      }
    }
    addField(new DecNumber(buffer, 36, 2, ITM_MIN_LEVEL));
    addField(new DecNumber(buffer, 38, 2, ITM_MIN_STRENGTH));
    if (ResourceFactory.resourceExists("KIT.IDS")) {
      addField(new DecNumber(buffer, 40, 1, ITM_MIN_STRENGTH_BONUS));
      addField(new Flag(buffer, 41, 1, ITM_UNUSABLE_BY_1, s_kituse1));
      addField(new DecNumber(buffer, 42, 1, ITM_MIN_INTELLIGENCE));
      addField(new Flag(buffer, 43, 1, ITM_UNUSABLE_BY_2, s_kituse2));
      addField(new DecNumber(buffer, 44, 1, ITM_MIN_DEXTERITY));
      addField(new Flag(buffer, 45, 1, ITM_UNUSABLE_BY_3, s_kituse3));
      addField(new DecNumber(buffer, 46, 1, ITM_MIN_WISDOM));
      addField(new Flag(buffer, 47, 1, ITM_UNUSABLE_BY_4, s_kituse4));
      addField(new DecNumber(buffer, 48, 1, ITM_MIN_CONSTITUTION));
      if (ResourceFactory.resourceExists("PROFTYPE.IDS")) {
        addField(new IdsBitmap(buffer, 49, 1, ITM_WEAPON_PROFICIENCY, "PROFTYPE.IDS"));
      } else {
        addField(new IdsBitmap(buffer, 49, 1, ITM_WEAPON_PROFICIENCY, "STATS.IDS"));
      }
    }
    else {
      addField(new DecNumber(buffer, 40, 2, ITM_MIN_STRENGTH_BONUS));
      addField(new DecNumber(buffer, 42, 2, ITM_MIN_INTELLIGENCE));
      addField(new DecNumber(buffer, 44, 2, ITM_MIN_DEXTERITY));
      addField(new DecNumber(buffer, 46, 2, ITM_MIN_WISDOM));
      addField(new DecNumber(buffer, 48, 2, ITM_MIN_CONSTITUTION));
    }
    addField(new DecNumber(buffer, 50, 2, ITM_MIN_CHARISMA));
    addField(new DecNumber(buffer, 52, 4, ITM_PRICE));
    addField(new DecNumber(buffer, 56, 2, ITM_MAX_IN_STACK));
    addField(new ResourceRef(buffer, 58, ITM_ICON, "BAM"));
    addField(new DecNumber(buffer, 66, 2, ITM_LORE));
    addField(new ResourceRef(buffer, 68, ITM_ICON_GROUND, "BAM"));
    addField(new DecNumber(buffer, 76, 4, ITM_WEIGHT));
    addField(new StringRef(buffer, 80, ITM_DESCRIPTION_GENERAL));
    addField(new StringRef(buffer, 84, ITM_DESCRIPTION_IDENTIFIED));
    if (version.toString().equalsIgnoreCase("V1.1")) {
      addField(new ResourceRef(buffer, 88, ITM_PICK_UP_SOUND, "WAV"));
    } else {
      if (Profile.isEnhancedEdition()) {
        addField(new ResourceRef(buffer, 88, ITM_DESCRIPTION_IMAGE, new String[]{"BAM", "BMP"}));
      } else {
        addField(new ResourceRef(buffer, 88, ITM_DESCRIPTION_IMAGE, "BAM"));
      }
    }
    addField(new DecNumber(buffer, 96, 4, ITM_ENCHANTMENT));
    SectionOffset abil_offset = new SectionOffset(buffer, 100, ITM_OFFSET_ABILITIES,
                                                  Ability.class);
    addField(abil_offset);
    SectionCount abil_count = new SectionCount(buffer, 104, 2, ITM_NUM_ABILITIES,
                                               Ability.class);
    addField(abil_count);
    SectionOffset global_offset = new SectionOffset(buffer, 106, ITM_OFFSET_EFFECTS,
                                                    Effect.class);
    addField(global_offset);
    addField(new DecNumber(buffer, 110, 2, ITM_FIRST_EFFECT_INDEX));
    SectionCount global_count = new SectionCount(buffer, 112, 2, ITM_NUM_GLOBAL_EFFECTS,
                                                 Effect.class);
    addField(global_count);

    if (version.toString().equalsIgnoreCase("V1.1")) {
      addField(new ResourceRef(buffer, 114, ITM_DIALOG, "DLG"));
      addField(new StringRef(buffer, 122, ITM_SPEAKER_NAME));
      addField(new IdsBitmap(buffer, 126, 2, ITM_WEAPON_COLOR, "CLOWNCLR.IDS"));
      addField(new Unknown(buffer, 128, 26));
    }
    else if (version.toString().equalsIgnoreCase("V2.0")) {
      addField(new Unknown(buffer, 114, 16));
    }

    offset = abil_offset.getValue();
    Ability abilities[] = new Ability[abil_count.getValue()];
    for (int i = 0; i < abilities.length; i++) {
      abilities[i] = new Ability(this, buffer, offset, i);
      offset = abilities[i].getEndOffset();
      addField(abilities[i]);
    }

    int offset2 = global_offset.getValue();
    for (int i = 0; i < global_count.getValue(); i++) {
      Effect eff = new Effect(this, buffer, offset2, i);
      offset2 = eff.getEndOffset();
      addField(eff);
    }

    for (final Ability ability : abilities)
      offset2 = ability.readEffects(buffer, offset2);

    return Math.max(offset, offset2);
  }


  // Called by "Extended Search"
  // Checks whether the specified resource entry matches all available search options.
  public static boolean matchSearchOptions(ResourceEntry entry, SearchOptions searchOptions)
  {
    if (entry != null && searchOptions != null) {
      try {
        ItmResource itm = new ItmResource(entry);
        Ability[] abilities;
        Effect[][] abilityEffects;
        Effect[] effects;
        boolean retVal = true;
        String key;
        Object o;

        // preparing substructures
        DecNumber ofs = (DecNumber)itm.getAttribute(ITM_OFFSET_EFFECTS, false);
        DecNumber cnt = (DecNumber)itm.getAttribute(ITM_NUM_GLOBAL_EFFECTS, false);
        if (ofs != null && ofs.getValue() > 0 && cnt != null && cnt.getValue() > 0) {
          effects = new Effect[cnt.getValue()];
          for (int idx = 0; idx < cnt.getValue(); idx++) {
            String label = String.format(SearchOptions.getResourceName(SearchOptions.ITM_Effect), idx);
            effects[idx] = (Effect)itm.getAttribute(label, false);
          }
        } else {
          effects = new Effect[0];
        }

        ofs = (DecNumber)itm.getAttribute(ITM_OFFSET_ABILITIES, false);
        cnt = (DecNumber)itm.getAttribute(ITM_NUM_ABILITIES, false);
        if (ofs != null && ofs.getValue() > 0 && cnt != null && cnt.getValue() > 0) {
          abilities = new Ability[cnt.getValue()];
          for (int idx = 0; idx < cnt.getValue(); idx++) {
            String label = String.format(SearchOptions.getResourceName(SearchOptions.ITM_Ability), idx);
            abilities[idx] = (Ability)itm.getAttribute(label, false);
          }
        } else {
          abilities = new Ability[0];
        }

        abilityEffects = new Effect[abilities.length][];
        for (int idx = 0; idx < abilities.length; idx++) {
          if (abilities[idx] != null) {
            cnt = (DecNumber)abilities[idx].getAttribute(AbstractAbility.ABILITY_NUM_EFFECTS, false);
            if (cnt != null && cnt.getValue() > 0) {
              abilityEffects[idx] = new Effect[cnt.getValue()];
              for (int idx2 = 0; idx2 < cnt.getValue(); idx2++) {
                String label = String.format(SearchOptions.getResourceName(SearchOptions.ITM_Ability_Effect), idx2);
                abilityEffects[idx][idx2] = (Effect)abilities[idx].getAttribute(label, false);
              }
            } else {
              abilityEffects[idx] = new Effect[0];
            }
          } else {
            abilityEffects[idx] = new Effect[0];
          }
        }

        // checking options
        if (retVal) {
          key = SearchOptions.ITM_Name;
          o = searchOptions.getOption(key);
          StructEntry struct = itm.getAttribute(SearchOptions.getResourceName(key), false);
          retVal &= SearchOptions.Utils.matchString(struct, o, false, false);
        }

        if (retVal) {
          key = SearchOptions.ITM_Appearance;
          o = searchOptions.getOption(key);
          StructEntry struct = itm.getAttribute(SearchOptions.getResourceName(key), false);
          retVal &= SearchOptions.Utils.matchString(struct, o, true, true);
        }

        String[] keyList = new String[]{SearchOptions.ITM_Flags, SearchOptions.ITM_Unusable,
                                        SearchOptions.ITM_KitsUnusable1, SearchOptions.ITM_KitsUnusable2,
                                        SearchOptions.ITM_KitsUnusable3, SearchOptions.ITM_KitsUnusable4};
        for (int idx = 0; idx < keyList.length; idx++) {
          if (retVal) {
            key = keyList[idx];
            o = searchOptions.getOption(key);
            StructEntry struct = itm.getAttribute(SearchOptions.getResourceName(key), false);
            retVal &= SearchOptions.Utils.matchFlags(struct, o);
          } else {
            break;
          }
        }

        keyList = new String[]{SearchOptions.ITM_Category, SearchOptions.ITM_Price,
                               SearchOptions.ITM_Enchantment, SearchOptions.ITM_MinLevel,
                               SearchOptions.ITM_MinSTR, SearchOptions.ITM_MinSTRExtra,
                               SearchOptions.ITM_MinCON, SearchOptions.ITM_MinDEX,
                               SearchOptions.ITM_MinINT, SearchOptions.ITM_MinWIS,
                               SearchOptions.ITM_MinCHA};
        for (int idx = 0; idx < keyList.length; idx++) {
          if (retVal) {
            key = keyList[idx];
            o = searchOptions.getOption(key);
            StructEntry struct = itm.getAttribute(SearchOptions.getResourceName(key), false);
            retVal &= SearchOptions.Utils.matchNumber(struct, o);
          } else {
            break;
          }
        }

        keyList = new String[]{SearchOptions.ITM_Effect_Type1, SearchOptions.ITM_Effect_Type2,
                               SearchOptions.ITM_Effect_Type3};
        for (int idx = 0; idx < keyList.length; idx++) {
          if (retVal) {
            boolean found = false;
            key = keyList[idx];
            o = searchOptions.getOption(key);
            for (int idx2 = 0; idx2 < effects.length; idx2++) {
              if (!found) {
                if (effects[idx2] != null) {
                  StructEntry struct = effects[idx2].getAttribute(SearchOptions.getResourceName(key), false);
                  found |= SearchOptions.Utils.matchNumber(struct, o);
                }
              } else {
                break;
              }
            }
            retVal &= found || (o == null);
          } else {
            break;
          }
        }

        SearchOptions abilityOption = (SearchOptions)searchOptions.getOption(SearchOptions.ITM_Ability);
        if (retVal && abilityOption != null) {
          // indicates whether any ability options have been selected
          boolean hasAbilityOptions = false;
          keyList = new String[]{SearchOptions.ITM_Ability_Type, SearchOptions.ITM_Ability_Target,
                                 SearchOptions.ITM_Ability_Range, SearchOptions.ITM_Ability_Launcher,
                                 SearchOptions.ITM_Ability_Speed, SearchOptions.ITM_Ability_DiceCount,
                                 SearchOptions.ITM_Ability_DiceSize, SearchOptions.ITM_Ability_Charges,
                                 SearchOptions.ITM_Ability_DamageType,
                                 SearchOptions.ITM_Ability_Projectile,
                                 SearchOptions.ITM_Ability_Flags,
                                 SearchOptions.ITM_Ability_Effect_Type1, SearchOptions.ITM_Ability_Effect_Type2,
                                 SearchOptions.ITM_Ability_Effect_Type3};
          for (int i = 0; i < keyList.length; i++) {
            hasAbilityOptions |= (abilityOption.getOption(keyList[i]) != null);
          }

          // tracks matches for each option in every available ability
          final int abilityOptions = keyList.length;    // number of supported spell ability options
          boolean[][] abilityMatches = new boolean[abilities.length][abilityOptions];
          for (int i = 0; i < abilities.length; i++) {
            for (int j = 0; j < abilityMatches[i].length; j++) {
              abilityMatches[i][j] = false;
            }
          }

          for (int i = 0; i < abilities.length; i++) {
            if (abilities[i] != null) {
              for (int j = 0; j < 10; j++) {
                key = keyList[j];
                o = abilityOption.getOption(key);
                StructEntry struct = abilities[i].getAttribute(SearchOptions.getResourceName(key), false);
                abilityMatches[i][j] = SearchOptions.Utils.matchNumber(struct, o);
              }

              {
                key = keyList[10];
                o = abilityOption.getOption(key);
                StructEntry struct = abilities[i].getAttribute(SearchOptions.getResourceName(key), false);
                abilityMatches[i][10] = SearchOptions.Utils.matchFlags(struct, o);
              }

              for (int j = 11; j < keyList.length; j++) {
                key = keyList[j];
                o = abilityOption.getOption(key);
                for (int k = 0; k < abilityEffects[i].length; k++) {
                  if (abilityEffects[i][k] != null) {
                    StructEntry struct = abilityEffects[i][k].getAttribute(SearchOptions.getResourceName(key), false);
                    abilityMatches[i][j] |= SearchOptions.Utils.matchNumber(struct, o);
                  }
                }
              }
            }
          }

          // evaluating collected results
          boolean[] foundSingle = new boolean[abilityMatches.length];    // for single ability option
          for (int i = 0; i < foundSingle.length; i++)
          {
            foundSingle[i] = false;
          }
          boolean[] foundMulti = new boolean[abilityOptions];           // for multiple abilities option
          for (int i = 0; i < foundMulti.length; i++)
          {
            foundMulti[i] = (abilityOption.getOption(keyList[i]) == null);
          }

          for (int i = 0; i < abilityMatches.length; i++) {
            if (abilities[i] != null) {
              foundSingle[i] = true;
              for (int j = 0; j < abilityMatches[i].length; j++) {
                foundSingle[i] &= abilityMatches[i][j];
                foundMulti[j] |= abilityMatches[i][j];
              }
            }
          }

          boolean resultSingle = false;
          for (int i = 0; i < foundSingle.length; i++) { resultSingle |= foundSingle[i]; }
          resultSingle |= !hasAbilityOptions;

          boolean resultMulti = true;
          for (int i = 0; i < foundMulti.length; i++) { resultMulti &= foundMulti[i]; }
          resultMulti |= !hasAbilityOptions;

          Boolean isAbilitySingle;
          o = abilityOption.getOption(SearchOptions.ITM_Ability_MatchSingle);
          if (o != null && o instanceof Boolean) {
            isAbilitySingle = (Boolean)o;
          } else {
            isAbilitySingle = false;
          }

          if (isAbilitySingle) {
            retVal &= resultSingle;
          } else {
            retVal &= resultMulti;
          }
        }

        keyList = new String[]{SearchOptions.ITM_Custom1, SearchOptions.ITM_Custom2,
                               SearchOptions.ITM_Custom3, SearchOptions.ITM_Custom4};
        for (int idx = 0; idx < keyList.length; idx++) {
          if (retVal) {
            key = keyList[idx];
            o = searchOptions.getOption(key);
            retVal &= SearchOptions.Utils.matchCustomFilter(itm, o);
          } else {
            break;
          }
        }


        return retVal;
      } catch (Exception e) {
      }
    }
    return false;
  }
}

