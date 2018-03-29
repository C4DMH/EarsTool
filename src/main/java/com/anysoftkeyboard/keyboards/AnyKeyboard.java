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
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Xml;
import android.view.inputmethod.EditorInfo;

import com.anysoftkeyboard.AnySoftKeyboard;
import com.anysoftkeyboard.addons.AddOn;
import com.anysoftkeyboard.api.KeyCodes;
import com.anysoftkeyboard.keyboardextensions.KeyboardExtension;
import com.anysoftkeyboard.keyboardextensions.KeyboardExtensionFactory;
import com.anysoftkeyboard.keyboards.views.KeyDrawableStateProvider;
import com.anysoftkeyboard.quicktextkeys.QuickTextKey;
import com.anysoftkeyboard.quicktextkeys.QuickTextKeyFactory;
import com.anysoftkeyboard.utils.Log;
import com.anysoftkeyboard.utils.Workarounds;
import com.menny.android.anysoftkeyboard.AnyApplication;
import com.sevencupsoftea.ears.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//import com.menny.android.anysoftkeyboard.R;

public abstract class AnyKeyboard extends Keyboard {
    private final static String TAG = "ASK - AK";

    public interface HardKeyboardAction {
        int getKeyCode();

        boolean isAltActive();

        boolean isShiftActive();

        void setNewKeyCode(int keyCode);
    }

    public interface HardKeyboardTranslator {
        /*
         * Gets the current state of the hard keyboard, and may change the
         * output key-code.
         */
        void translatePhysicalCharacter(HardKeyboardAction action,
                                        AnySoftKeyboard ime);
    }

    private static final String TAG_ROW = "Row";
    private static final String TAG_KEY = "Key";

    private static class KeyboardMetadata {
        public int keysCount = 0;
        public int rowHeight = 0;
        public int rowWidth = 0;
        public int verticalGap = 0;
        public boolean isTopRow = false;
    }

    private static final int STICKY_KEY_OFF = 0;
    private static final int STICKY_KEY_ON = 1;
    private static final int STICKY_KEY_LOCKED = 2;

    private int mShiftState = STICKY_KEY_OFF;
    private int mControlState = STICKY_KEY_OFF;

    private Key mShiftKey;
    private Key mControlKey;
    private EnterKey mEnterKey;
    // public Key langSwitch;

    private boolean mRightToLeftLayout = false;// the "super" ctor will create
    // keys, and we'll set the
    // correct value there.

    private boolean mTopRowWasCreated;
    private boolean mBottomRowWasCreated;

    private int mGenericRowsHeight = 0;
    private int mTopRowKeysCount = 0;
    // max(generic row widths)
    private int mMaxGenericRowsWidth = 0;

    private KeyboardCondenser mKeyboardCondenser;

    // private int mKeyboardActionType = EditorInfo.IME_ACTION_NONE;

    // for popup keyboard
    // note: the context can be from a different package!
    protected AnyKeyboard(@NonNull AddOn keyboardAddOn, @NonNull Context askContext, @NonNull Context context, int xmlLayoutResId) {
        // should use the package context for creating the layout
        super(keyboardAddOn, askContext, context, xmlLayoutResId, -1);
        // no generic rows in popup
    }

    // for the External
    // note: the context can be from a different package!
    protected AnyKeyboard(@NonNull AddOn keyboardAddOn, @NonNull Context askContext, @NonNull Context context, int xmlLayoutResId, int mode) {
        // should use the package context for creating the layout
        super(keyboardAddOn, askContext, context, xmlLayoutResId, mode);
    }

    public void loadKeyboard(final KeyboardDimens keyboardDimens) {
        final KeyboardExtension topRowPlugin = KeyboardExtensionFactory.getCurrentKeyboardExtension(mASKContext, KeyboardExtension.TYPE_TOP);
        final KeyboardExtension bottomRowPlugin = KeyboardExtensionFactory.getCurrentKeyboardExtension(mASKContext, KeyboardExtension.TYPE_BOTTOM);

        loadKeyboard(keyboardDimens, topRowPlugin, bottomRowPlugin);
    }

    public void loadKeyboard(final KeyboardDimens keyboardDimens, @Nullable KeyboardExtension topRowPlugin, @NonNull KeyboardExtension bottomRowPlugin) {
        super.loadKeyboard(keyboardDimens);

        addGenericRows(mKeyboardMode, keyboardDimens, topRowPlugin, bottomRowPlugin);
        initKeysMembers(mASKContext);
    }


    public void onKeyboardViewWidthChanged(int newWidth, int oldWidth) {
        if (oldWidth == 0) oldWidth = mDisplayWidth;
        mDisplayWidth = newWidth;
        final double zoomFactor = ((double)newWidth) / ((double)oldWidth);
        for (Key key : getKeys()) {
            key.x = (int) (zoomFactor * key.x);
            key.width = (int) (zoomFactor * key.width);
        }
    }

    private void initKeysMembers(Context askContext) {
        for (final Key key : getKeys()) {
            if (key.y == 0)
                key.edgeFlags |= Keyboard.EDGE_TOP;

            if (key.codes.length > 0) {
                final int primaryCode = key.getPrimaryCode();
                if (key instanceof AnyKey) {
                    switch (primaryCode) {
                        case KeyCodes.DELETE:
                        case KeyCodes.MODE_ALPHABET:
                        case KeyCodes.KEYBOARD_MODE_CHANGE:
                        case KeyCodes.KEYBOARD_CYCLE:
                        case KeyCodes.KEYBOARD_CYCLE_INSIDE_MODE:
                        case KeyCodes.KEYBOARD_REVERSE_CYCLE:
                        case KeyCodes.ALT:
                        case KeyCodes.MODE_SYMOBLS:
                        case KeyCodes.QUICK_TEXT:
                        case KeyCodes.DOMAIN:
                        case KeyCodes.CANCEL:
                        case KeyCodes.CTRL:
                            ((AnyKey) key).setAsFunctional();
                            break;
                    }
                }

                // detecting LTR languages
                if (Workarounds.isRightToLeftCharacter((char) primaryCode))
                    mRightToLeftLayout = true;// one is enough
                switch (primaryCode) {
                    case KeyCodes.QUICK_TEXT:
                        QuickTextKey quickKey = QuickTextKeyFactory
                                .getCurrentQuickTextKey(askContext);
                        if (quickKey == null) { // No plugins. Weird, but we
                            // can't do anything
                            Log.w(TAG, "Could not locate any quick key plugins! Hopefully nothing will crash...");
                            break;
                        }

                        Resources quickTextKeyResources = quickKey.getPackageContext().getResources();

                        key.label = quickKey.getKeyLabel();

                        int iconResId = quickKey.getKeyIconResId();
                        int previewResId = quickKey.getIconPreviewResId();
                        if (iconResId > 0) {
                            setKeyIcons(key, quickTextKeyResources, iconResId, previewResId);
                        }

                    /*
                     * Popup resource may be from another context, requires
                     * special handling when the key is long-pressed!
                     */
                        key.popupResId = quickKey.getPopupKeyboardResId();
                        key.externalResourcePopupLayout = key.popupResId != 0;
                        break;
                    case KeyCodes.DOMAIN:
                        // fixing icons
                        key.label = AnyApplication.getConfig().getDomainText()
                                .trim();
                        key.popupResId = R.xml.popup_domains;
                        break;
                    default:
                        // setting the character label
                        if (isAlphabetKey(key) && (key.icon == null)) {
                            final boolean labelIsOriginallyEmpty = TextUtils
                                    .isEmpty(key.label);
                            if (labelIsOriginallyEmpty) {
                                final char code = (char) key.codes[0];
                                // check the ASCII table, everything below 32,
                                // is not printable
                                if (code > 31 && !Character.isWhitespace(code))
                                    key.label = Character.toString(code);
                            }
                        }
                }
            }
        }

        mKeyboardCondenser = new KeyboardCondenser(askContext, this);
    }

    protected void addGenericRows(int mode, final KeyboardDimens keyboardDimens, @Nullable KeyboardExtension topRowPlugin, @NonNull KeyboardExtension bottomRowPlugin) {
        final KeyboardMetadata topMd;
        if (!mTopRowWasCreated) {
            if (topRowPlugin == null || topRowPlugin.getKeyboardResId() == AddOn.INVALID_RES_ID) {
                Log.d(TAG, "No top row layout");
                topMd = null;
                // adding EDGE_TOP to top keys. See issue 775
                List<Key> keys = getKeys();
                for (int keyIndex = 0; keyIndex < keys.size(); keyIndex++) {
                    final Key key = keys.get(keyIndex);
                    if (key.y == 0)
                        key.edgeFlags = key.edgeFlags | Keyboard.EDGE_TOP;
                }
            } else {
                Log.d(TAG, "Top row layout id %s", topRowPlugin.getId());
                topMd = addKeyboardRow(topRowPlugin.getResourceMapping(), topRowPlugin.getPackageContext(),
                        topRowPlugin.getKeyboardResId(), mode, keyboardDimens);
            }

            if (topMd != null)
                fixKeyboardDueToGenericRow(topMd, (int) keyboardDimens.getRowVerticalGap());
        }
        if (!mBottomRowWasCreated) {
            Log.d(TAG, "Bottom row layout id %s", bottomRowPlugin.getId());
            KeyboardMetadata bottomMd = addKeyboardRow(bottomRowPlugin.getResourceMapping(), bottomRowPlugin.getPackageContext(),
                    bottomRowPlugin.getKeyboardResId(), mode, keyboardDimens);
            fixKeyboardDueToGenericRow(bottomMd,
                    (int) keyboardDimens.getRowVerticalGap());
        }
    }

    private void fixKeyboardDueToGenericRow(KeyboardMetadata md,
                                            int rowVerticalGap) {
        final int additionalPixels = (md.rowHeight + md.verticalGap + rowVerticalGap);
        mGenericRowsHeight += additionalPixels;
        if (md.isTopRow) {
            mTopRowKeysCount += md.keysCount;
            List<Key> keys = getKeys();
            for (int keyIndex = md.keysCount; keyIndex < keys.size(); keyIndex++) {
                final Key key = keys.get(keyIndex);
                key.y += additionalPixels;
            }
        }
    }

    private KeyboardMetadata addKeyboardRow(@NonNull AddOn.AddOnResourceMapping resourceMapping, Context context, int rowResId, int mode, final KeyboardDimens keyboardDimens) {
        XmlResourceParser parser = context.getResources().getXml(rowResId);
        List<Key> keys = getKeys();
        boolean inKey = false;
        boolean inRow = false;
        // boolean leftMostKey = false;
        boolean skipRow;

        final float keyHorizontalGap = keyboardDimens.getKeyHorizontalGap();
        final float rowVerticalGap = keyboardDimens.getRowVerticalGap();
        float x = 0;
        float y = rowVerticalGap;
        Key key = null;
        Row currentRow = null;
        Resources res = context.getResources();

        KeyboardMetadata m = new KeyboardMetadata();

        try {
            int event;
            while ((event = parser.next()) != XmlResourceParser.END_DOCUMENT) {
                if (event == XmlResourceParser.START_TAG) {
                    String tag = parser.getName();
                    if (TAG_ROW.equals(tag)) {
                        inRow = true;
                        x = 0;
                        currentRow = createRowFromXml(resourceMapping, res, parser);
                        skipRow = currentRow.mode != 0 && currentRow.mode != mode;
                        if (skipRow) {
                            currentRow = null;
                            skipToEndOfRow(parser);
                            inRow = false;
                        } else {
                            m.isTopRow = currentRow.rowEdgeFlags == Keyboard.EDGE_TOP;
                            if (!m.isTopRow) {
                                // the bottom row Y should be last
                                // The last coordinate is height + keyboard's
                                // default vertical gap
                                // since mTotalHeight = y - mDefaultVerticalGap;
                                // (see loadKeyboard
                                // in the android sources)
                                // We use our overriden getHeight method which
                                // is just fixed so that it includes the first
                                // generic row.
                                y = getHeight() + getVerticalGap();
                            }
                            m.rowHeight = 0;
                            m.verticalGap = currentRow.verticalGap;
                        }
                    } else if (TAG_KEY.equals(tag)) {
                        inKey = true;
                        x += (keyHorizontalGap / 2);
                        key = createKeyFromXml(resourceMapping, mASKContext, context, currentRow, keyboardDimens, (int) x, (int) y, parser);
                        key.width -= keyHorizontalGap;// the gap is on both
                        // sides
                        if (m.isTopRow)
                            keys.add(m.keysCount, key);
                        else
                            keys.add(key);
                        m.keysCount++;

                        m.rowHeight = Math.max(key.height, m.rowHeight);
                    }
                } else if (event == XmlResourceParser.END_TAG) {
                    if (inKey) {
                        inKey = false;
                        x += (key.gap + key.width);
                        x += (keyHorizontalGap / 2);
                        if (x > m.rowWidth) {
                            m.rowWidth = (int) x;
                            // We keep generic row max width updated
                            mMaxGenericRowsWidth = Math.max(mMaxGenericRowsWidth, m.rowWidth);
                        }
                    } else if (inRow) {
                        inRow = false;
                        y += currentRow.verticalGap;
                        y += m.rowHeight;
                        y += rowVerticalGap;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Parse error:" + e);
            e.printStackTrace();
        }
        // mTotalHeight = y - mDefaultVerticalGap;
        return m;
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

    /* required overrides */

    @Override
    public int getHeight() {
        return super.getHeight() + mGenericRowsHeight;
    }

    // minWidth is actually 'total width', see android framework source code
    @Override
    public int getMinWidth() {
        return Math.max(mMaxGenericRowsWidth, super.getMinWidth());
    }

    @Override
    public int getShiftKeyIndex() {
        return super.getShiftKeyIndex() + mTopRowKeysCount;
    }

    private void setKeyIcons(Key key, Resources localResources, @DrawableRes int iconId,
                             int iconFeedbackId) {
        try {
            key.icon = localResources.getDrawable(iconId);
            if (iconFeedbackId > 0) {
                Drawable preview = localResources.getDrawable(iconFeedbackId);
                KeyboardSupport.updateDrawableBounds(preview);
                key.iconPreview = preview;
            }
        } catch (OutOfMemoryError m) {
            Log.w(TAG,
                    "Low memory when trying to load key icon. I'll leave it empty.");
        }

        if (key.icon != null)
            key.label = null;
    }

    public Context getKeyboardContext() {
        return mKeyboardContext;
    }

    public abstract String getDefaultDictionaryLocale();

    public Locale getLocale() {
        return Locale.getDefault();
    }

    // this function is called from within the super constructor.
    @Override
    protected Key createKeyFromXml(@NonNull AddOn.AddOnResourceMapping resourceMapping, Context askContext, Context keyboardContext,
                                   Row parent, KeyboardDimens keyboardDimens, int x, int y,
                                   XmlResourceParser parser) {
        AnyKey key = new AnyKey(resourceMapping, askContext, keyboardContext, parent, keyboardDimens, x, y, parser);

        if (key.codes.length > 0) {
            final int primaryCode = key.codes[0];

            // creating less sensitive keys if required
            switch (primaryCode) {
                case KeyCodes.DISABLED:// disabled
                    key.disable();
                    break;
                case KeyCodes.ENTER:// enter
                    key = mEnterKey = new EnterKey(resourceMapping, mASKContext, keyboardContext, parent, keyboardDimens, x, y, parser);
                    break;
                case KeyCodes.SHIFT:
                    mShiftKey = key;// I want the reference used by the super.
                    break;
                case KeyCodes.CTRL:
                    mControlKey = key;
                    break;
                // case KeyCodes.DELETE://delete
                // key = new LessSensitiveAnyKey(mASKContext, res, parent, x, y,
                // parser);
                // break;
            }
        }

        setPopupKeyChars(key);

        return key;
    }

    @Override
    protected Row createRowFromXml(@NonNull AddOn.AddOnResourceMapping resourceMapping, Resources res, XmlResourceParser parser) {
        Row aRow = super.createRowFromXml(resourceMapping, res, parser);
        if (aRow.mode > 0)
            aRow.mode = res.getInteger(aRow.mode);// switching to the mode!

        if ((aRow.rowEdgeFlags & Keyboard.EDGE_TOP) != 0)
            mTopRowWasCreated = true;
        if ((aRow.rowEdgeFlags & Keyboard.EDGE_BOTTOM) != 0)
            mBottomRowWasCreated = true;

        return aRow;
    }

    private boolean isAlphabetKey(Key key) {
        return (!key.modifier) && (!key.sticky) && (!key.repeatable) &&
        /* (key.icon == null) && */
                (key.getPrimaryCode() > 0);
    }

    public boolean isStartOfWordLetter(char keyValue) {
        return Character.isLetter(keyValue)/* || (keyValue == '\'') */;
    }

    public boolean isInnerWordLetter(char keyValue) {
        return Character.isLetter(keyValue) || (keyValue == '\'');
    }

    public abstract char[] getSentenceSeparators();

    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     */
    public void setImeOptions(Resources res, EditorInfo editor) {
        if (mEnterKey == null) {
            return;
        }

        mEnterKey.enable();
    }

    public abstract String getKeyboardName();

    public boolean isLeftToRightLanguage() {
        return !mRightToLeftLayout;
    }

    public abstract int getKeyboardIconResId();

    public boolean setShiftLocked(boolean shiftLocked) {
        if (keyboardSupportShift()) {
            final int initialState = mShiftState;
            if (shiftLocked) {
                mShiftState = STICKY_KEY_LOCKED;
            } else if (mShiftState == STICKY_KEY_LOCKED) {
                mShiftState = STICKY_KEY_ON;
            }

            setShiftViewAsState();
            return initialState != mShiftState;
        }

        return false;
    }

    @Override
    public boolean isShifted() {
        if (keyboardSupportShift()) {
            return mShiftState != STICKY_KEY_OFF;
        } else {
            return false;
        }
    }

    @Override
    public boolean setShifted(boolean shiftState) {
        if (keyboardSupportShift()) {
            final int initialState = mShiftState;
            if (shiftState) {
                if (mShiftState == STICKY_KEY_OFF) {// so it is not LOCKED
                    mShiftState = STICKY_KEY_ON;
                }
                // else this is already ON, or at caps lock.
            } else {
                mShiftState = STICKY_KEY_OFF;
            }

            setShiftViewAsState();
            return mShiftState != initialState;
        } else {
            super.setShifted(shiftState);
            return false;
        }
    }

    protected boolean keyboardSupportShift() {
        return mShiftKey != null;
    }

    private void setShiftViewAsState() {
        // the "on" led is just like the caps-lock led
        if (mShiftKey != null)
            mShiftKey.on = (mShiftState == STICKY_KEY_LOCKED);
    }

    public boolean isShiftLocked() {
        return mShiftState == STICKY_KEY_LOCKED;
    }

    public boolean isControl() {
        if (mControlKey != null) {
            return mControlState != STICKY_KEY_OFF;
        } else {
            return false;
        }
    }

    public boolean setControl(boolean control) {
        if (mControlKey != null) {
            final int initialState = mControlState;
            if (control) {
                if (mControlState == STICKY_KEY_OFF) {// so it is not LOCKED
                    mControlState = STICKY_KEY_ON;
                }
                // else this is already ON, or at caps lock.
            } else {
                mControlState = STICKY_KEY_OFF;
            }

            setControlViewAsState();
            return mControlState != initialState;
        } else {
            return false;
        }
    }

    private void setControlViewAsState() {
        // the "on" led is just like the caps-lock led
        mControlKey.on = (mControlState == STICKY_KEY_LOCKED);
    }

    /*
     * public boolean isControlLocked() { return mControlState ==
     * STICKY_KEY_LOCKED; }
     */
    protected void setPopupKeyChars(Key aKey) {

    }

    public static class AnyKey extends Keyboard.Key {

        @NonNull
        protected int[] shiftedCodes = new int[0];
        public CharSequence shiftedKeyLabel;
        public CharSequence hintLabel;
        public int longPressCode;
        private boolean mFunctionalKey;
        private boolean mEnabled;

        public AnyKey(Row row, KeyboardDimens keyboardDimens) {
            super(row, keyboardDimens);
        }

        public AnyKey(@NonNull AddOn.AddOnResourceMapping resourceMapping, Context askContext, Context keyboardContext, Keyboard.Row parent,
                      KeyboardDimens keyboardDimens, int x, int y, XmlResourceParser parser) {
            super(resourceMapping, askContext, keyboardContext, parent, keyboardDimens, x, y, parser);
            //setting up some defaults
            mEnabled = true;
            mFunctionalKey = false;
            longPressCode = 0;
            shiftedKeyLabel = null;
            hintLabel = null;

            TypedArray a = keyboardContext.obtainStyledAttributes(Xml.asAttributeSet(parser), resourceMapping.getRemoteStyleableArrayFromLocal(R.styleable.KeyboardLayout_Key));
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                final int remoteIndex = a.getIndex(i);
                final int localAttrId = R.styleable.KeyboardLayout_Key[remoteIndex];
                try {
                    switch (localAttrId) {
                        case R.attr.shiftedCodes:
                            shiftedCodes = KeyboardSupport.getKeyCodesFromTypedArray(a, remoteIndex);
                            break;
                        case R.attr.longPressCode:
                            longPressCode = a.getInt(remoteIndex, 0);
                            break;
                        case R.attr.isFunctional:
                            mFunctionalKey = a.getBoolean(remoteIndex, false);
                            break;
                        case R.attr.shiftedKeyLabel:
                            shiftedKeyLabel = a.getString(remoteIndex);
                            break;
                        case R.attr.hintLabel:
                            hintLabel = a.getString(remoteIndex);
                            break;
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Failed to set data from XML!", e);
                }
            }
            a.recycle();

            // ensuring codes and shiftedCodes are the same size
            if (shiftedCodes.length != codes.length) {
                int[] wrongSizedShiftCodes = shiftedCodes;
                shiftedCodes = new int[codes.length];
                int i;
                for (i = 0; i < wrongSizedShiftCodes.length && i < codes.length; i++)
                    shiftedCodes[i] = wrongSizedShiftCodes[i];
                for (/* starting from where i finished above */; i < codes.length; i++) {
                    final int code = codes[i];
                    if (Character.isLetter(code))
                        shiftedCodes[i] = Character.toUpperCase(code);
                    else
                        shiftedCodes[i] = code;
                }
            }

            if (popupCharacters != null && popupCharacters.length() == 0) {
                // If there is a keyboard with no keys specified in
                // popupCharacters
                popupResId = 0;
            }
        }

        public int getCodeAtIndex(int index, boolean isShifted) {
            return isShifted? shiftedCodes[index] : codes[index];
        }

        public void enable() {
            mEnabled = true;
        }

        public void disable() {
            iconPreview = null;
            icon = null;
            label = "  ";// can not use NULL.
            mEnabled = false;
        }

        public boolean isInside(int clickedX, int clickedY) {
            return mEnabled && super.isInside(clickedX, clickedY);
        }

        public void setAsFunctional() {
            mFunctionalKey = true;
        }

        @Override
        public int[] getCurrentDrawableState(KeyDrawableStateProvider provider) {
            if (mFunctionalKey) {
                if (pressed) {
                    return provider.KEY_STATE_FUNCTIONAL_PRESSED;
                } else {
                    return provider.KEY_STATE_FUNCTIONAL_NORMAL;
                }
            }
            return super.getCurrentDrawableState(provider);
        }
    }

    private static class EnterKey extends AnyKey {

        private final int mOriginalHeight;

        public EnterKey(@NonNull AddOn.AddOnResourceMapping resourceMapping, Context askContext, Context keyboardContext, Row parent,
                        KeyboardDimens keyboardDimens, int x, int y,
                        XmlResourceParser parser) {
            super(resourceMapping, askContext, keyboardContext, parent, keyboardDimens, x, y, parser);
            mOriginalHeight = this.height;
        }

        @Override
        public void disable() {
            if (AnyApplication.getConfig().getActionKeyInvisibleWhenRequested())
                this.height = 0;
            super.disable();
        }

        @Override
        public void enable() {
            this.height = mOriginalHeight;
            super.enable();
        }

        @Override
        public int[] getCurrentDrawableState(KeyDrawableStateProvider provider) {
            if (pressed) {
                return provider.KEY_STATE_ACTION_PRESSED;
            } else {
                return provider.KEY_STATE_ACTION_NORMAL;
            }
        }
    }

    @NonNull
    public abstract String getKeyboardPrefId();

    public boolean requiresProximityCorrection() {
        return getKeys().size() > 20;
    }

    public int getKeyboardMode() {
        return mKeyboardMode;
    }

    public void setCondensedKeys(CondenseType condenseType) {
        if (mKeyboardCondenser.setCondensedKeys(condenseType))
            computeNearestNeighbors();//keyboard has changed, so we need to recompute the neighbors.
    }
}
