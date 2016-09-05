/*
 * Copyright (c) 2013 Menny Even-Danan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package com.anysoftkeyboard.keyboards;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.util.Xml;

import com.anysoftkeyboard.addons.AddOn;
import com.anysoftkeyboard.api.KeyCodes;
import com.anysoftkeyboard.keyboards.views.KeyDrawableStateProvider;
import com.anysoftkeyboard.utils.Log;
import com.menny.android.anysoftkeyboard.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Keyboard {

    static final String TAG = "Keyboard";

    // Keyboard XML Tags
    private static final String TAG_KEYBOARD = "Keyboard";
    private static final String TAG_ROW = "Row";
    private static final String TAG_KEY = "Key";

    public static final int EDGE_LEFT = 0x01;
    public static final int EDGE_RIGHT = 0x02;
    public static final int EDGE_TOP = 0x04;
    public static final int EDGE_BOTTOM = 0x08;

    public static final int KEY_EMBLEM_NONE = 0x00;
    public static final int KEY_EMBLEM_TEXT = 0x01;
    public static final int KEY_EMBLEM_ICON = 0x02;

    protected final int mLayoutResId;

    @NonNull
    private final AddOn mAddOn;
    @NonNull
    protected final Context mKeyboardContext;
    @NonNull
    protected final Context mASKContext;
    @NonNull
    private final AddOn.AddOnResourceMapping mKeyboardResourceMap;

    /**
     * Horizontal gap default for all rows
     */
    private int mDefaultHorizontalGap;

    /**
     * Default key width
     */
    private int mDefaultWidth;

    /**
     * Default key height
     */
    private int mDefaultHeightCode;

    /**
     * Default gap between rows
     */
    private int mDefaultVerticalGap;

    /**
     * Is the keyboard in the shifted state
     */
    private boolean mShifted;

    /**
     * Key instance for the shift key, if present
     */
    private Key mShiftKey;

    /**
     * Key index for the shift key, if present
     */
    private int mShiftKeyIndex = -1;

    /**
     * Total height of the keyboard, including the padding and keys
     */
    private int mTotalHeight;

    /**
     * Total width of the keyboard, including left side gaps and keys, but not
     * any gaps on the right side.
     */
    private int mTotalWidth;

    /**
     * List of keys in this keyboard
     */
    private List<Key> mKeys;

    /**
     * List of modifier keys such as Shift & Alt, if any
     */
    private List<Key> mModifierKeys;

    /**
     * Width of the screen available to fit the keyboard
     */
    protected int mDisplayWidth;

    /** Height of the screen */
    // private int mDisplayHeight;

    /**
     * Keyboard mode, or zero, if none.
     */
    protected final int mKeyboardMode;

    // Variables for pre-computing nearest keys.

    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 5;
    private static final int GRID_SIZE = GRID_WIDTH * GRID_HEIGHT;
    private int mCellWidth;
    private int mCellHeight;
    private int[][] mGridNeighbors;
    private int mProximityThreshold;
    /**
     * Number of key widths from current touch point to search for nearest keys.
     */
    private static float SEARCH_DISTANCE = 1.8f;

    /**
     * Container for keys in the keyboard. All keys in a row are at the same
     * Y-coordinate. Some of the key size defaults can be overridden per row
     * from what the {@link Keyboard} defines.
     */
    public static class Row {
        /**
         * Default width of a key in this row.
         */
        public int defaultWidth;
        /**
         * Default height of a key in this row.
         */
        public int defaultHeightCode;
        /**
         * Default horizontal gap between keys in this row.
         */
        public int defaultHorizontalGap;
        /**
         * Vertical gap following this row.
         */
        public int verticalGap;
        /**
         * Edge flags for this row of keys. Possible values that can be assigned
         * are {@link Keyboard#EDGE_TOP EDGE_TOP} and
         * {@link Keyboard#EDGE_BOTTOM EDGE_BOTTOM}
         */
        public int rowEdgeFlags;

        /**
         * The keyboard mode for this row
         */
        public int mode;

        protected Keyboard parent;

        public Row(Keyboard parent) {
            this.parent = parent;

            defaultWidth = parent.mDefaultWidth;
            defaultHeightCode = parent.mDefaultHeightCode;

            defaultHorizontalGap = parent.mDefaultHorizontalGap;
            verticalGap = parent.getVerticalGap();

            rowEdgeFlags = EDGE_TOP + EDGE_BOTTOM;
            mode = parent.mKeyboardMode;
        }

        public Row(@NonNull final AddOn.AddOnResourceMapping resourceMap, Resources res, Keyboard parent, XmlResourceParser parser) {
            this.parent = parent;
            //some defaults
            defaultWidth = parent.mDefaultWidth;
            defaultHeightCode = parent.mDefaultHeightCode;
            defaultHorizontalGap = parent.mDefaultHorizontalGap;
            verticalGap = parent.getVerticalGap();
            //now reading from the XML
            int[] remoteKeyboardLayoutStyleable = resourceMap.getRemoteStyleableArrayFromLocal(R.styleable.KeyboardLayout);
            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), remoteKeyboardLayoutStyleable);
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                final int remoteIndex = a.getIndex(i);
                final int localAttrId = R.styleable.KeyboardLayout[remoteIndex];
                try {
                    switch (localAttrId) {
                        case android.R.attr.keyWidth:
                            defaultWidth = getDimensionOrFraction(a, remoteIndex, parent.mDisplayWidth, parent.mDefaultWidth);
                            break;
                        case android.R.attr.keyHeight:
                            defaultHeightCode = getKeyHeightCode(a, remoteIndex, parent.mDefaultHeightCode);
                            break;
                        case android.R.attr.horizontalGap:
                            defaultHorizontalGap = getDimensionOrFraction(a, remoteIndex, parent.mDisplayWidth, parent.mDefaultHorizontalGap);
                            break;
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Failed to set data from XML!", e);
                }
            }
            a.recycle();
            int[] remoteKeyboardRowLayoutStyleable = resourceMap.getRemoteStyleableArrayFromLocal(R.styleable.KeyboardLayout_Row);
            a = res.obtainAttributes(Xml.asAttributeSet(parser), remoteKeyboardRowLayoutStyleable);
            n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                final int remoteIndex = a.getIndex(i);
                final int localAttrId = R.styleable.KeyboardLayout_Row[remoteIndex];
                try {
                    switch (localAttrId) {
                        case android.R.attr.rowEdgeFlags:
                            rowEdgeFlags = a.getInt(remoteIndex, 0);
                            break;
                        case android.R.attr.keyboardMode:
                            mode = a.getResourceId(remoteIndex, 0);
                            break;
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Failed to set data from XML!", e);
                }
            }
            a.recycle();
        }
    }

    /**
     * Class for describing the position and characteristics of a single key in
     * the keyboard.
     */
    public static abstract class Key {
        /**
         * All the key codes (unicode or custom code) that this key could
         * generate, zero'th being the most important.
         */
        @NonNull
        protected int[] codes = new int[0];

        /**
         * Label to display
         */
        public CharSequence label;

        /**
         * Icon to display instead of a label. Icon takes precedence over a
         * label
         */
        public Drawable icon;
        /**
         * Preview version of the icon, for the preview popup
         */
        public Drawable iconPreview;
        /**
         * Width of the key, not including the gap
         */
        public int width;
        /**
         * Height of the key, not including the gap
         */
        public int height;
        /**
         * The horizontal gap before this key
         */
        public int gap;
        /**
         * Whether this key is sticky, i.e., a toggle key
         */
        public boolean sticky;
        /**
         * X coordinate of the key in the keyboard layout
         */
        public int x;
        /**
         * Y coordinate of the key in the keyboard layout
         */
        public int y;
        /**
         * The current pressed state of this key
         */
        public boolean pressed;
        /**
         * If this is a sticky key, is it on?
         */
        public boolean on;
        /**
         * Text to output when pressed. This can be multiple characters, like
         * ".com"
         */
        public CharSequence text;
        /**
         * Popup characters
         */
        public CharSequence popupCharacters;

        /**
         * Flags that specify the anchoring to edges of the keyboard for
         * detecting touch events that are just out of the boundary of the key.
         * This is a bit mask of {@link Keyboard#EDGE_LEFT},
         * {@link Keyboard#EDGE_RIGHT}, {@link Keyboard#EDGE_TOP} and
         * {@link Keyboard#EDGE_BOTTOM}.
         */
        public int edgeFlags;
        /**
         * Whether this is a modifier key, such as Shift or Alt
         */
        public boolean modifier;
        /**
         * The keyboard that this key belongs to
         */
        private Keyboard keyboard;

        public final Row row;
        /**
         * If this key pops up a mini keyboard, this is the resource id for the
         * XML layout for that keyboard.
         */
        public int popupResId;

        public boolean externalResourcePopupLayout = false;
        /**
         * Whether this key repeats itself when held down
         */
        public boolean repeatable;
        
        /**
         * Whether this key should show previewPopup
         */
        public boolean showPreview;

        public int dynamicEmblem;

        /**
         * Create an empty key with no attributes.
         */
        public Key(Row parent, KeyboardDimens keyboardDimens) {
            row = parent;
            keyboard = parent.parent;
            height = KeyboardSupport.getKeyHeightFromHeightCode(keyboardDimens, parent.defaultHeightCode, row.parent.mASKContext.getResources().getConfiguration().orientation);
            width = Math.min(keyboardDimens.getKeyMaxWidth(), parent.defaultWidth);
            gap = parent.defaultHorizontalGap;
            edgeFlags = parent.rowEdgeFlags;
        }

        /**
         * Create a key with the given top-left coordinate and extract its
         * attributes from the XML parser.
         *
         * @param parent the row that this key belongs to. The row must already
         *               be attached to a {@link Keyboard}.
         * @param x      the x coordinate of the top-left
         * @param y      the y coordinate of the top-left
         * @param parser the XML parser containing the attributes for this key
         */
        public Key(@NonNull AddOn.AddOnResourceMapping resourceMapping, Context askContext, Context keyboardContext, Row parent,
                   KeyboardDimens keyboardDimens, int x, int y, XmlResourceParser parser) {
            this(parent, keyboardDimens);
            final Resources askResources = askContext.getResources();
            this.x = x;
            this.y = y;

            //setting up some defaults
            width = Math.min(keyboardDimens.getKeyMaxWidth(), parent.defaultWidth);
            height = KeyboardSupport.getKeyHeightFromHeightCode(keyboardDimens, parent.defaultHeightCode, askResources.getConfiguration().orientation);
            gap = parent.defaultHorizontalGap;
            codes = new int[0];
            iconPreview = null;
            popupCharacters = null;
            popupResId = 0;
            repeatable = false;
            showPreview = true;
            dynamicEmblem = KEY_EMBLEM_NONE;
            modifier = false;
            sticky = false;

            //loading data from XML
            int[] remoteKeyboardLayoutStyleable = resourceMapping.getRemoteStyleableArrayFromLocal(R.styleable.KeyboardLayout);
            TypedArray a = keyboardContext.obtainStyledAttributes(Xml.asAttributeSet(parser), remoteKeyboardLayoutStyleable);
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                final int remoteIndex = a.getIndex(i);
                final int localAttrId = R.styleable.KeyboardLayout[remoteIndex];
                setDataFromTypedArray(parent, keyboardDimens, askResources, a, remoteIndex, localAttrId);
            }
            a.recycle();
            this.x += gap;

            int[] remoteKeyboardKeyLayoutStyleable = resourceMapping.getRemoteStyleableArrayFromLocal(R.styleable.KeyboardLayout_Key);
            a = keyboardContext.obtainStyledAttributes(Xml.asAttributeSet(parser),
                    remoteKeyboardKeyLayoutStyleable);
            n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                final int remoteIndex = a.getIndex(i);
                final int localAttrId = R.styleable.KeyboardLayout_Key[remoteIndex];
                setDataFromTypedArray(parent, keyboardDimens, askResources, a, remoteIndex, localAttrId);
            }
            externalResourcePopupLayout = popupResId != 0;
            if (codes.length == 0 && !TextUtils.isEmpty(label)) {
                codes = new int[]{ label.charAt(0) };
            }
            a.recycle();
        }

        private void setDataFromTypedArray(Row parent, KeyboardDimens keyboardDimens, Resources askResources, TypedArray a, int remoteIndex, int localAttrId) {
            try {
                switch (localAttrId) {
                    case android.R.attr.keyWidth:
                        width = getDimensionOrFraction(a,
                                remoteIndex,
                                keyboard.mDisplayWidth, parent.defaultWidth);
                        width = Math.min(keyboardDimens.getKeyMaxWidth(), width);
                        break;
                    case android.R.attr.keyHeight:
                        int heightCode = getKeyHeightCode(a, remoteIndex, parent.defaultHeightCode);
                        height = KeyboardSupport.getKeyHeightFromHeightCode(keyboardDimens, heightCode, askResources.getConfiguration().orientation);
                        break;
                    case android.R.attr.horizontalGap:
                        gap = getDimensionOrFraction(a, remoteIndex,
                            keyboard.mDisplayWidth, parent.defaultHorizontalGap);
                        break;
                    case android.R.attr.codes:
                        codes = KeyboardSupport.getKeyCodesFromTypedArray(a, remoteIndex);
                        break;
                    case android.R.attr.iconPreview:
                        iconPreview = a.getDrawable(remoteIndex);
                        KeyboardSupport.updateDrawableBounds(iconPreview);
                        break;
                    case android.R.attr.popupCharacters:
                        popupCharacters = a.getText(remoteIndex);
                        break;
                    case android.R.attr.popupKeyboard:
                        popupResId = a.getResourceId(remoteIndex, 0);
                        break;
                    case android.R.attr.isRepeatable:
                        repeatable = a.getBoolean(remoteIndex, false);
                        break;
                    case R.attr.showPreview:
                        showPreview = a.getBoolean(remoteIndex, true);
                        break;
                    case R.attr.keyDynamicEmblem:
                        dynamicEmblem = a.getInt(remoteIndex, KEY_EMBLEM_NONE);
                        break;
                    case android.R.attr.isModifier:
                        modifier = a.getBoolean(remoteIndex, false);
                        break;
                    case android.R.attr.isSticky:
                        sticky = a.getBoolean(remoteIndex, false);
                        break;
                    case android.R.attr.keyEdgeFlags:
                        edgeFlags = a.getInt(remoteIndex, 0);
                        edgeFlags |= parent.rowEdgeFlags;
                        break;
                    case android.R.attr.keyIcon:
                        icon = a.getDrawable(remoteIndex);
                        KeyboardSupport.updateDrawableBounds(icon);
                        break;
                    case android.R.attr.keyLabel:
                        label = a.getText(remoteIndex);
                        break;
                    case android.R.attr.keyOutputText:
                        text = a.getText(remoteIndex);
                        break;
                }
            } catch(Exception e){
                Log.w(TAG, "Failed to load keyboard layout! ", e);
            }
        }

        public int getPrimaryCode() {
            return codes.length > 0? codes[0] : 0;
        }

        public int getCodeAtIndex(int index, boolean isShifted) {
            return codes[index];
        }

        public int getCodesCount() {
            return codes.length;
        }

        /**
         * Informs the key that it has been pressed, in case it needs to change
         * its appearance or state.
         *
         * @see #onReleased(boolean)
         */
        public void onPressed() {
            pressed = true;
        }

        /**
         * Changes the pressed state of the key. If it is a sticky key, it will
         * also change the toggled state of the key if the finger was release
         * inside.
         *
         * @param inside whether the finger was released inside the key
         * @see #onPressed()
         */
        public void onReleased(boolean inside) {
            pressed = false;
        }

        /**
         * Detects if a point falls inside this key.
         *
         * @param x the x-coordinate of the point
         * @param y the y-coordinate of the point
         * @return whether or not the point falls inside the key. If the key is
         *         attached to an edge, it will assume that all points between
         *         the key and the edge are considered to be inside the key.
         */
        public boolean isInside(int x, int y) {
            boolean leftEdge = (edgeFlags & EDGE_LEFT) > 0;
            boolean rightEdge = (edgeFlags & EDGE_RIGHT) > 0;
            boolean topEdge = (edgeFlags & EDGE_TOP) > 0;
            boolean bottomEdge = (edgeFlags & EDGE_BOTTOM) > 0;
            if ((x >= this.x || (leftEdge && x <= this.x + this.width))
                    && (x < this.x + this.width || (rightEdge && x >= this.x))
                    && (y >= this.y || (topEdge && y <= this.y + this.height))
                    && (y < this.y + this.height || (bottomEdge && y >= this.y))) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Returns the square of the distance between the closest point inside
         * the key and the given point.
         *
         * @param x the x-coordinate of the point
         * @param y the y-coordinate of the point
         * @return the square of the distance of the point from and the key
         */
        public int squaredDistanceFrom(int x, int y) {
            final int closestX = (x < this.x) ? this.x
                    : (x > (this.x + this.width)) ? (this.x + this.width) : x;
            final int closestY = (y < this.y) ? this.y
                    : (y > (this.y + this.height)) ? (this.y + this.height) : y;
            final int xDist = closestX - x;
            final int yDist = closestY - y;
            /*
             * int xDist = this.x + width / 2 - x; int yDist = this.y + height /
             * 2 - y;
             */
            return xDist * xDist + yDist * yDist;
        }

        /**
         * Returns the drawable state for the key, based on the current state
         * and type of the key.
         *
         * @return the drawable state of the key.
         * @see android.graphics.drawable.StateListDrawable#setState(int[])
         */
        public int[] getCurrentDrawableState(KeyDrawableStateProvider provider) {
            int[] states = provider.KEY_STATE_NORMAL;

            if (on) {
                if (pressed) {
                    states = provider.KEY_STATE_PRESSED_ON;
                } else {
                    states = provider.KEY_STATE_NORMAL_ON;
                }
            } else {
                if (sticky) {
                    if (pressed) {
                        states = provider.KEY_STATE_PRESSED_OFF;
                    } else {
                        states = provider.KEY_STATE_NORMAL_OFF;
                    }
                } else {
                    if (pressed) {
                        states = provider.KEY_STATE_PRESSED;
                    }
                }
            }
            return states;
        }
    }

    /**
     * Creates a keyboard from the given xml key layout file.
     *
     * @param context        the application or service context
     * @param xmlLayoutResId the resource file that contains the keyboard layout
     *                       and keys.
     */
    public Keyboard(@NonNull AddOn keyboardAddOn, @NonNull Context askContext, @NonNull Context context, int xmlLayoutResId) {
        this(keyboardAddOn, askContext, context, xmlLayoutResId, 0);
    }

    protected static int getKeyHeightCode(TypedArray a, int remoteIndex, int defaultHeightCode) {
        TypedValue value = a.peekValue(remoteIndex);
        if (value == null) {
            // means that it was not provided. So I take my parent's
            return defaultHeightCode;
        } else if (value.type >= TypedValue.TYPE_FIRST_INT && value.type <= TypedValue.TYPE_LAST_INT &&
                value.data <= 0 && value.data >= -3) {
            return value.data;
        } else {
            Log.w(TAG, "Key height attribute is incorrectly set! Defaulting to regular height.");
            return -1;
        }
    }

    /**
     * Creates a keyboard from the given xml key layout file. Weeds out rows
     * that have a keyboard mode defined but don't match the specified mode.
     *
     * @param context        the application or service context
     * @param xmlLayoutResId the resource file that contains the keyboard layout
     *                       and keys.
     * @param modeId         keyboard mode identifier
     */
    public Keyboard(@NonNull AddOn keyboardAddOn, @NonNull  Context askContext, @NonNull Context context, int xmlLayoutResId, int modeId) {
        mAddOn = keyboardAddOn;
        mKeyboardResourceMap = keyboardAddOn.getResourceMapping();

        mASKContext = askContext;
        mKeyboardContext = context;
        mLayoutResId = xmlLayoutResId;
        mKeyboardMode = modeId;

        mKeys = new ArrayList<>();
        mModifierKeys = new ArrayList<>();
    }

    public AddOn getKeyboardAddOn() {
        return mAddOn;
    }

    public List<Key> getKeys() {
        return mKeys;
    }

    public List<Key> getModifierKeys() {
        return mModifierKeys;
    }

    protected int getHorizontalGap() {
        return mDefaultHorizontalGap;
    }

    protected void setHorizontalGap(int gap) {
        mDefaultHorizontalGap = gap;
    }

    protected int getVerticalGap() {
        return mDefaultVerticalGap;
    }

    /**
     * Returns the total height of the keyboard
     *
     * @return the total height of the keyboard
     */
    public int getHeight() {
        return mTotalHeight;
    }

    public int getMinWidth() {
        return mTotalWidth;
    }

    public void resetDimensions() {
        mTotalWidth = 0;
        mTotalHeight = 0;
        for (Key key : mKeys) {
            int x = key.x + key.gap + key.width;
            if (x > mTotalWidth) {
                mTotalWidth = x;
            }
            int y = key.y + key.height;
            if (y > mTotalHeight) {
                mTotalHeight = y;
            }
        }
    }

    public boolean setShifted(boolean shiftState) {
        if (mShiftKey != null) {
            mShiftKey.on = shiftState;
        }
        if (mShifted != shiftState) {
            mShifted = shiftState;
            return true;
        }
        return false;
    }

    public boolean isShifted() {
        return mShifted;
    }

    public int getShiftKeyIndex() {
        return mShiftKeyIndex;
    }

    protected final void computeNearestNeighbors() {
        // Round-up so we don't have any pixels outside the grid
        mCellWidth = (getMinWidth() + GRID_WIDTH - 1) / GRID_WIDTH;
        mCellHeight = (getHeight() + GRID_HEIGHT - 1) / GRID_HEIGHT;
        mGridNeighbors = new int[GRID_SIZE][];
        int[] indices = new int[mKeys.size()];
        final int gridWidth = GRID_WIDTH * mCellWidth;
        final int gridHeight = GRID_HEIGHT * mCellHeight;
        for (int x = 0; x < gridWidth; x += mCellWidth) {
            for (int y = 0; y < gridHeight; y += mCellHeight) {
                int count = 0;
                for (int i = 0; i < mKeys.size(); i++) {
                    final Key key = mKeys.get(i);
                    if (key.squaredDistanceFrom(x, y) < mProximityThreshold ||
                            key.squaredDistanceFrom(x + mCellWidth - 1, y) < mProximityThreshold ||
                            key.squaredDistanceFrom(x + mCellWidth - 1, y + mCellHeight - 1)
                                    < mProximityThreshold ||
                            key.squaredDistanceFrom(x, y + mCellHeight - 1) < mProximityThreshold) {
                        indices[count++] = i;
                    }
                }
                int[] cell = new int[count];
                System.arraycopy(indices, 0, cell, 0, count);
                mGridNeighbors[(y / mCellHeight) * GRID_WIDTH + (x / mCellWidth)] = cell;
            }
        }
    }

    /**
     * Returns the indices of the keys that are closest to the given point.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the array of integer indices for the nearest keys to the given
     *         point. If the given point is out of range, then an array of size
     *         zero is returned.
     */
    public int[] getNearestKeys(int x, int y) {
        if (mGridNeighbors == null)
            computeNearestNeighbors();
        if (x >= 0 && x < getMinWidth() && y >= 0 && y < getHeight()) {
            int index = (y / mCellHeight) * GRID_WIDTH + (x / mCellWidth);
            if (index < GRID_SIZE) {
                return mGridNeighbors[index];
            }
        }
        return new int[0];
    }

    protected Row createRowFromXml(@NonNull AddOn.AddOnResourceMapping resourceMapping, Resources res, XmlResourceParser parser) {
        return new Row(resourceMapping, res, this, parser);
    }

    protected abstract Key createKeyFromXml(@NonNull AddOn.AddOnResourceMapping resourceMapping, Context askContext, Context keyboardContext,
                                            Row parent, KeyboardDimens keyboardDimens, int x, int y,
                                            XmlResourceParser parser);

    public void loadKeyboard(final KeyboardDimens keyboardDimens) {
        mDisplayWidth = keyboardDimens.getKeyboardMaxWidth();
        final float rowVerticalGap = keyboardDimens.getRowVerticalGap();
        final float keyHorizontalGap = keyboardDimens.getKeyHorizontalGap();

        mDefaultHorizontalGap = 0;
        mDefaultWidth = mDisplayWidth / 10;
        mDefaultHeightCode = -1;

        XmlResourceParser parser = mKeyboardContext.getResources().getXml(mLayoutResId);
        boolean inKey = false;
        boolean inRow = false;
        boolean inUnknown = false;
        int row = 0;
        float x = 0;
        float y = rowVerticalGap;// starts with a gap
        int rowHeight = 0;
        Key key = null;
        Row currentRow = null;
        Resources res = mKeyboardContext.getResources();
        boolean skipRow = false;
        int lastVerticalGap = 0;

        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG) {
                    String tag = parser.getName();
                    if (TAG_ROW.equals(tag)) {
                        inRow = true;
                        x = 0;
                        rowHeight = 0;
                        currentRow = createRowFromXml(mKeyboardResourceMap, res, parser);
                        skipRow = currentRow.mode != 0 && currentRow.mode != mKeyboardMode;
                        if (skipRow) {
                            skipToEndOfRow(parser);
                            inRow = false;
                        }
                    } else if (TAG_KEY.equals(tag)) {
                        inKey = true;
                        x += (keyHorizontalGap / 2);
                        key = createKeyFromXml(mKeyboardResourceMap, mASKContext, mKeyboardContext, currentRow, keyboardDimens,
                                (int) x, (int) y, parser);
                        rowHeight = Math.max(rowHeight, key.height);
                        key.width -= keyHorizontalGap;// the gap is on both
                        // sides
                        mKeys.add(key);
                        if (key.getPrimaryCode() == KeyCodes.SHIFT) {
                            mShiftKey = key;
                            mShiftKeyIndex = mKeys.size() - 1;
                            mModifierKeys.add(key);
                        } else if (key.getPrimaryCode() == KeyCodes.ALT) {
                            mModifierKeys.add(key);
                        }
                    } else if (TAG_KEYBOARD.equals(tag)) {
                        parseKeyboardAttributes(mASKContext, res, parser);
                    } else {
                        inUnknown = true;
                        Log.w(TAG, "Unknown tag '%s' while parsing keyboard!", tag);
                    }
                } else if (event == XmlResourceParser.END_TAG) {
                    if (inKey) {
                        inKey = false;
                        x += key.gap + key.width;
                        x += (keyHorizontalGap / 2);
                        if (x > mTotalWidth) {
                            mTotalWidth = (int) x;
                        }
                    } else if (inRow) {
                        inRow = false;
                        lastVerticalGap = currentRow.verticalGap;
                        y += currentRow.verticalGap;
                        y += rowHeight;
                        y += rowVerticalGap;
                        row++;
                    } else if (inUnknown) {
                        inUnknown = false;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Parse error:" + e);
            e.printStackTrace();
        }
        mTotalHeight = (int) (y - lastVerticalGap);
    }

    private void skipToEndOfRow(XmlResourceParser parser)
            throws XmlPullParserException, IOException {
        int event;
        while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
            if (event == XmlResourceParser.END_TAG
                    && parser.getName().equals(TAG_ROW)) {
                break;
            }
        }
    }

    private void parseKeyboardAttributes(Context askContext, Resources res, XmlResourceParser parser) {
        final AddOn.AddOnResourceMapping addOnResourceMapping = mKeyboardResourceMap;
        int[] remoteKeyboardLayoutStyleable = addOnResourceMapping.getRemoteStyleableArrayFromLocal(R.styleable.KeyboardLayout);
        TypedArray a = res.obtainAttributes(Xml.asAttributeSet(parser), remoteKeyboardLayoutStyleable);
        Resources askRes = askContext.getResources();
        //some defaults
        mDefaultWidth = mDisplayWidth / 10;
        mDefaultHeightCode = -1;
        mDefaultHorizontalGap = 0;
        mDefaultVerticalGap = askRes.getDimensionPixelOffset(R.dimen.default_key_vertical_gap);
        //now reading from XML
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            final int remoteIndex = a.getIndex(i);
            final int localAttrId = R.styleable.KeyboardLayout[remoteIndex];
            try {
                switch (localAttrId) {
                    case android.R.attr.keyWidth:
                        mDefaultWidth = getDimensionOrFraction(a, remoteIndex, mDisplayWidth, mDisplayWidth / 10);
                        break;
                    case android.R.attr.keyHeight:
                        mDefaultHeightCode = getKeyHeightCode(a, remoteIndex, -1);
                        break;
                    case android.R.attr.horizontalGap:
                        mDefaultHorizontalGap = getDimensionOrFraction(a, remoteIndex,
                            mDisplayWidth, 0);
                        break;
                    /*vertical gap is part of the Theme, not the keyboard.*/
                    /*case android.R.attr.verticalGap:
                        mDefaultVerticalGap = getDimensionOrFraction(a, remoteIndex, mDisplayWidth, mDefaultVerticalGap);
                        break;*/
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to set data from XML!", e);
            }
        }
        a.recycle();

        mProximityThreshold = (int) (mDefaultWidth * SEARCH_DISTANCE);
        // Square it for comparison
        mProximityThreshold = mProximityThreshold * mProximityThreshold;
    }

    static int getDimensionOrFraction(TypedArray a, int index, int base, int defValue) {
        TypedValue value = a.peekValue(index);
        if (value == null)
            return defValue;
        if (value.type == TypedValue.TYPE_DIMENSION) {
            return a.getDimensionPixelOffset(index, defValue);
        } else if (value.type == TypedValue.TYPE_FRACTION) {
            // Round it to avoid values like 47.9999 from getting truncated
            return Math.round(a.getFraction(index, base, base, defValue));
        }
        return defValue;
    }
}
