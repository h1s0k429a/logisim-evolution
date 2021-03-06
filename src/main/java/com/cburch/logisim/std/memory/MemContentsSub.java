/**
 * This file is part of logisim-evolution.
 *
 * Logisim-evolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Logisim-evolution is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with logisim-evolution. If not, see <http://www.gnu.org/licenses/>.
 *
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * Subsequent modifications by:
 *   + College of the Holy Cross
 *     http://www.holycross.edu
 *   + Haute École Spécialisée Bernoise/Berner Fachhochschule
 *     http://www.bfh.ch
 *   + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *     http://hepia.hesge.ch/
 *   + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *     http://www.heig-vd.ch/
 */

package com.cburch.logisim.std.memory;

import com.cburch.logisim.prefs.AppPreferences;

class MemContentsSub {
  private static class BytePage extends MemContents.Page {
    private byte[] data;
    private int mask;

    public BytePage(int size, int mask) {
      this.mask = mask;
      data = new byte[size];
      if (AppPreferences.Memory_Startup_Unknown.get()) {
        java.util.Random generator = new java.util.Random();
        for (int i = 0; i < size; i++) {
          data[i] = (byte) (generator.nextInt(256) & mask);
        }
      }
    }

    @Override
    public BytePage clone() {
      BytePage ret = (BytePage) super.clone();
      ret.data = new byte[this.data.length];
      System.arraycopy(this.data, 0, ret.data, 0, this.data.length);
      return ret;
    }

    @Override
    int get(int addr) {
      return addr >= 0 && addr < data.length ? data[addr] : 0;
    }

    //
    // methods for accessing data within memory
    //
    @Override
    int getLength() {
      return data.length;
    }

    @Override
    void load(int start, int[] values, int mask) {
      int n = Math.min(values.length, data.length - start);
      for (int i = 0; i < n; i++) {
        data[start + i] = (byte) (values[i] & mask);
      }
    }

    @Override
    void set(int addr, int value) {
      if (addr >= 0 && addr < data.length) {
        byte oldValue = data[addr];
        if (value != oldValue) {
          data[addr] = (byte) value;
        }
      }
    }
  }

  private static class IntPage extends MemContents.Page {
    private int[] data;
    private int mask;

    public IntPage(int size, int mask) {
      this.mask = mask;
      data = new int[size];
      if (AppPreferences.Memory_Startup_Unknown.get()) {
        java.util.Random generator = new java.util.Random();
        for (int i = 0; i < size; i++) data[i] = (int) generator.nextInt() & mask;
      }
    }

    @Override
    public IntPage clone() {
      IntPage ret = (IntPage) super.clone();
      ret.data = new int[this.data.length];
      System.arraycopy(this.data, 0, ret.data, 0, this.data.length);
      return ret;
    }

    @Override
    int get(int addr) {
      return addr >= 0 && addr < data.length ? data[addr] : 0;
    }

    //
    // methods for accessing data within memory
    //
    @Override
    int getLength() {
      return data.length;
    }

    @Override
    void load(int start, int[] values, int mask) {
      int n = Math.min(values.length, data.length - start);
      for (int i = 0; i < n; i++) {
        data[start+i] = values[i] & mask;
      }
    }

    @Override
    void set(int addr, int value) {
      if (addr >= 0 && addr < data.length) {
        int oldValue = data[addr];
        if (value != oldValue) {
          data[addr] = value;
        }
      }
    }
  }

  private static class ShortPage extends MemContents.Page {
    private short[] data;
    private int mask;

    public ShortPage(int size, int mask) {
      data = new short[size];
      this.mask = mask;
      if (AppPreferences.Memory_Startup_Unknown.get()) {
        java.util.Random generator = new java.util.Random();
        for (int i = 0; i < size; i++) data[i] = (short) (generator.nextInt(1 << 16) & mask);
      }
    }

    @Override
    public ShortPage clone() {
      ShortPage ret = (ShortPage) super.clone();
      ret.data = new short[this.data.length];
      System.arraycopy(this.data, 0, ret.data, 0, this.data.length);
      return ret;
    }

    @Override
    int get(int addr) {
      return addr >= 0 && addr < data.length ? data[addr] : 0;
    }

    //
    // methods for accessing data within memory
    //
    @Override
    int getLength() {
      return data.length;
    }

    @Override
    void load(int start, int[] values, int mask) {
      int n = Math.min(values.length, data.length - start);
      /*
       * Bugfix in memory writing (by Roy77)
       * https://github.com/roy77
       */
      for (int i = start; i < n; i++) {
        data[start + i] = (short) (values[i] & mask);
      }
    }

    @Override
    void set(int addr, int value) {
      if (addr >= 0 && addr < data.length) {
        short oldValue = data[addr];
        if (value != oldValue) {
          data[addr] = (short) value;
        }
      }
    }
  }

  static MemContents.Page createPage(int size, int bits) {
    int mask = (bits == 32) ? 0xffffffff : (1 << bits) - 1;
    if (bits <= 8) return new BytePage(size, mask);
    else if (bits <= 16) return new ShortPage(size, mask);
    else return new IntPage(size, mask);
  }

  private MemContentsSub() {}
}
