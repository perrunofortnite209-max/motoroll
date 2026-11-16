package com.motoroll.home;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(8, 11, 20);
    private static final int PANEL = Color.rgb(17, 29, 50);
    private static final int PANEL2 = Color.rgb(21, 36, 61);
    private static final int TEXT = Color.rgb(242, 246, 255);
    private static final int MUTED = Color.rgb(151, 167, 192);
    private static final int BLUE = Color.rgb(36, 107, 254);
    private static final int CYAN = Color.rgb(84, 185, 255);
    private static final int PURPLE = Color.rgb(129, 84, 232);

    private final List<ResolveInfo> launchableApps = new ArrayList<>();
    private LinearLayout base;
    private EditText drawerSearch;
    private boolean drawerOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureWindow();
        loadApps();
        showHome();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApps();
        if (!drawerOpen) showHome();
    }

    private void configureWindow() {
        Window w = getWindow();
        w.setStatusBarColor(BG);
        w.setNavigationBarColor(BG);
        w.getDecorView().setSystemUiVisibility(0);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable rounded(int color, int radiusDp, int strokeColor, int strokeDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        if (strokeDp > 0) d.setStroke(dp(strokeDp), strokeColor);
        return d;
    }

    private GradientDrawable gradient(int start, int end, int radiusDp) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{start, end});
        d.setCornerRadius(dp(radiusDp));
        return d;
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setFontFeatureSettings("kern");
        if (bold) t.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD));
        else t.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private LinearLayout.LayoutParams lp(int width, int height) {
        return new LinearLayout.LayoutParams(width < 0 ? width : dp(width), height < 0 ? height : dp(height));
    }

    private void applySafeInsets(View root) {
        final int left = dp(18), right = dp(18), top = dp(12), bottom = dp(10);
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            int topInset = 0, bottomInset = 0;
            if (Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                topInset = bars.top;
                bottomInset = bars.bottom;
            } else {
                topInset = insets.getSystemWindowInsetTop();
                bottomInset = insets.getSystemWindowInsetBottom();
            }
            v.setPadding(left, top + topInset, right, bottom + bottomInset);
            return insets;
        });
        root.requestApplyInsets();
    }

    private void loadApps() {
        launchableApps.clear();
        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> found = pm.queryIntentActivities(query, 0);
        for (ResolveInfo info : found) {
            if (info.activityInfo == null) continue;
            if (getPackageName().equals(info.activityInfo.packageName)) continue;
            launchableApps.add(info);
        }
        Collator collator = Collator.getInstance();
        Collections.sort(launchableApps, (a, b) -> collator.compare(
                String.valueOf(a.loadLabel(pm)), String.valueOf(b.loadLabel(pm))));
    }

    private void showHome() {
        drawerOpen = false;
        base = column();
        base.setBackgroundColor(BG);
        applySafeInsets(base);
        setContentView(base);

        LinearLayout top = row();
        top.setGravity(Gravity.CENTER_VERTICAL);
        MotorollMark mark = new MotorollMark(this);
        top.addView(mark, lp(46, 46));
        LinearLayout brand = column();
        brand.setPadding(dp(12), 0, 0, 0);
        TextView title = text("MOTOROLL", 17, TEXT, true);
        TextView subtitle = text("OS  •  HOME 1.0", 9, MUTED, true);
        brand.addView(title);
        brand.addView(subtitle);
        top.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        TextView settings = text("⚙", 24, CYAN, false);
        settings.setGravity(Gravity.CENTER);
        settings.setBackground(rounded(PANEL, 18, Color.rgb(34, 62, 100), 1));
        top.addView(settings, lp(46, 46));
        settings.setOnClickListener(v -> openSettings());
        base.addView(top, lp(-1, 52));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        LinearLayout content = column();
        content.setPadding(0, dp(22), 0, dp(18));
        scroll.addView(content);
        base.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        TextView hello = text("TU ESPACIO. TU SISTEMA.", 10, CYAN, true);
        hello.setLetterSpacing(0.12f);
        content.addView(hello);

        TextClock clock = new TextClock(this);
        clock.setFormat24Hour("HH:mm");
        clock.setFormat12Hour("h:mm");
        clock.setTextColor(TEXT);
        clock.setTextSize(54);
        clock.setTypeface(android.graphics.Typeface.create("sans-serif-light", 0));
        LinearLayout.LayoutParams clockParams = lp(-2, -2);
        clockParams.topMargin = dp(4);
        content.addView(clock, clockParams);

        TextClock date = new TextClock(this);
        date.setFormat12Hour("EEEE, d MMMM");
        date.setFormat24Hour("EEEE, d MMMM");
        date.setTextColor(MUTED);
        date.setTextSize(13);
        content.addView(date, lp(-2, -2));

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(14);
        search.setTextColor(TEXT);
        search.setHintTextColor(MUTED);
        search.setHint("Buscar aplicaciones");
        search.setCompoundDrawablePadding(dp(10));
        search.setPadding(dp(16), dp(12), dp(16), dp(12));
        search.setBackground(rounded(PANEL, 18, Color.rgb(37, 62, 98), 1));
        LinearLayout.LayoutParams searchParams = lp(-1, 50);
        searchParams.topMargin = dp(22);
        content.addView(search, searchParams);
        search.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) showDrawer(""); });
        search.setOnClickListener(v -> showDrawer(""));

        LinearLayout section = row();
        section.setGravity(Gravity.CENTER_VERTICAL);
        TextView quickTitle = text("ACCESOS RÁPIDOS", 11, MUTED, true);
        quickTitle.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams st = new LinearLayout.LayoutParams(0, -2, 1);
        st.topMargin = dp(24);
        section.addView(quickTitle, st);
        content.addView(section);

        GridLayout quick = new GridLayout(this);
        quick.setColumnCount(2);
        quick.setUseDefaultMargins(false);
        addQuickTile(quick, "Ajustes", "⚙", "Conexiones y sistema", this::openSettings);
        addQuickTile(quick, "Cámara", "◉", "Captura un momento", this::openCamera);
        addQuickTile(quick, "Modo gaming", "◇", "Herramientas de juego", this::openGameMode);
        addQuickTile(quick, "Personalizar", "✦", "Aspecto Motoroll", this::showPersonalize);
        LinearLayout.LayoutParams quickParams = lp(-1, -2);
        quickParams.topMargin = dp(12);
        content.addView(quick, quickParams);

        LinearLayout appsHeader = row();
        TextView appsTitle = text("TUS APLICACIONES", 11, MUTED, true);
        appsTitle.setLetterSpacing(0.08f);
        appsHeader.addView(appsTitle, new LinearLayout.LayoutParams(0, -2, 1));
        TextView seeAll = text("Ver todas  ›", 12, CYAN, true);
        appsHeader.addView(seeAll);
        seeAll.setOnClickListener(v -> showDrawer(""));
        LinearLayout.LayoutParams appsHeaderParams = lp(-1, -2);
        appsHeaderParams.topMargin = dp(24);
        content.addView(appsHeader, appsHeaderParams);

        GridLayout appsGrid = new GridLayout(this);
        appsGrid.setColumnCount(4);
        appsGrid.setUseDefaultMargins(false);
        int count = Math.min(8, launchableApps.size());
        for (int i = 0; i < count; i++) appsGrid.addView(createAppCell(launchableApps.get(i)), gridCellParams());
        LinearLayout.LayoutParams gridParams = lp(-1, -2);
        gridParams.topMargin = dp(12);
        content.addView(appsGrid, gridParams);

        LinearLayout dock = row();
        dock.setGravity(Gravity.CENTER);
        dock.setBackground(rounded(Color.rgb(14, 22, 38), 24, Color.rgb(31, 52, 83), 1));
        addDockButton(dock, "Teléfono", "☎", this::openDialer);
        addDockButton(dock, "Mensajes", "✉", this::openMessages);
        addDockButton(dock, "Cámara", "◉", this::openCamera);
        addDockButton(dock, "Apps", "▦", () -> showDrawer(""));
        LinearLayout.LayoutParams dockParams = lp(-1, 70);
        dockParams.topMargin = dp(10);
        base.addView(dock, dockParams);
    }

    private GridLayout.LayoutParams gridCellParams() {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = dp(94);
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(dp(2), dp(4), dp(2), dp(4));
        return p;
    }

    private void addQuickTile(GridLayout grid, String name, String glyph, String description, Runnable action) {
        LinearLayout tile = column();
        tile.setGravity(Gravity.CENTER_VERTICAL);
        tile.setPadding(dp(14), dp(12), dp(14), dp(12));
        tile.setBackground(rounded(PANEL, 18, Color.rgb(32, 53, 84), 1));
        TextView symbol = text(glyph, 22, CYAN, true);
        tile.addView(symbol);
        TextView title = text(name, 13, TEXT, true);
        LinearLayout.LayoutParams t = lp(-1, -2); t.topMargin = dp(5); tile.addView(title, t);
        TextView desc = text(description, 10, MUTED, false);
        tile.addView(desc);
        tile.setOnClickListener(v -> action.run());
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0; p.height = dp(104); p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(dp(3), dp(3), dp(3), dp(3));
        grid.addView(tile, p);
    }

    private View createAppCell(ResolveInfo info) {
        LinearLayout cell = column();
        cell.setGravity(Gravity.CENTER);
        cell.setPadding(dp(2), dp(5), dp(2), dp(5));
        ImageView icon = new ImageView(this);
        try { icon.setImageDrawable(info.loadIcon(getPackageManager())); } catch (Exception ignored) {}
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        cell.addView(icon, lp(45, 45));
        TextView label = text(String.valueOf(info.loadLabel(getPackageManager())), 10, TEXT, false);
        label.setGravity(Gravity.CENTER);
        label.setMaxLines(1);
        label.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams labelParams = lp(-1, -2); labelParams.topMargin = dp(5);
        cell.addView(label, labelParams);
        cell.setOnClickListener(v -> launchApp(info));
        return cell;
    }

    private void addDockButton(LinearLayout dock, String label, String glyph, Runnable action) {
        LinearLayout item = column();
        item.setGravity(Gravity.CENTER);
        TextView icon = text(glyph, 21, CYAN, true);
        icon.setGravity(Gravity.CENTER);
        item.addView(icon, lp(-1, 31));
        TextView name = text(label, 9, MUTED, false);
        name.setGravity(Gravity.CENTER);
        item.addView(name, lp(-1, 17));
        item.setOnClickListener(v -> action.run());
        dock.addView(item, new LinearLayout.LayoutParams(0, -1, 1));
    }

    private void showDrawer(String initialQuery) {
        drawerOpen = true;
        base = column();
        base.setBackgroundColor(BG);
        applySafeInsets(base);
        setContentView(base);
        LinearLayout header = row();
        TextView back = text("‹", 34, CYAN, false);
        back.setGravity(Gravity.CENTER);
        header.addView(back, lp(40, 48));
        back.setOnClickListener(v -> showHome());
        LinearLayout titleCol = column();
        TextView title = text("Aplicaciones", 20, TEXT, true);
        TextView subtitle = text("MOTOROLL OS", 9, MUTED, true);
        titleCol.addView(title); titleCol.addView(subtitle);
        header.addView(titleCol, new LinearLayout.LayoutParams(0, -2, 1));
        base.addView(header, lp(-1, 52));

        drawerSearch = new EditText(this);
        drawerSearch.setSingleLine(true);
        drawerSearch.setTextColor(TEXT);
        drawerSearch.setHintTextColor(MUTED);
        drawerSearch.setTextSize(14);
        drawerSearch.setHint("Buscar una aplicación");
        drawerSearch.setPadding(dp(16), dp(12), dp(16), dp(12));
        drawerSearch.setBackground(rounded(PANEL, 18, Color.rgb(37, 62, 98), 1));
        if (initialQuery != null && !initialQuery.isEmpty()) drawerSearch.setText(initialQuery);
        LinearLayout.LayoutParams sp = lp(-1, 50); sp.topMargin = dp(16); base.addView(drawerSearch, sp);

        ScrollView scroll = new ScrollView(this);
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setUseDefaultMargins(false);
        scroll.addView(grid);
        LinearLayout.LayoutParams sc = new LinearLayout.LayoutParams(-1, 0, 1);
        sc.topMargin = dp(14); base.addView(scroll, sc);

        Runnable refresh = () -> {
            grid.removeAllViews();
            String q = drawerSearch.getText() == null ? "" : drawerSearch.getText().toString().toLowerCase(Locale.ROOT).trim();
            for (ResolveInfo info : launchableApps) {
                String label = String.valueOf(info.loadLabel(getPackageManager()));
                if (q.isEmpty() || label.toLowerCase(Locale.ROOT).contains(q)) {
                    grid.addView(createAppCell(info), gridCellParams());
                }
            }
        };
        refresh.run();
        drawerSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { refresh.run(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void showPersonalize() {
        LinearLayout page = column(); page.setBackgroundColor(BG); applySafeInsets(page); setContentView(page);
        TextView back = text("‹  Motoroll Home", 18, CYAN, true); back.setPadding(0, dp(8), 0, dp(16)); back.setOnClickListener(v -> showHome()); page.addView(back);
        TextView head = text("Personalizar", 28, TEXT, true); page.addView(head);
        TextView desc = text("La identidad visual de tu sistema comienza aquí.", 13, MUTED, false); page.addView(desc);
        addInfoCard(page, "Paleta Motoroll", "Negro AMOLED · Azul eléctrico · Violeta");
        addInfoCard(page, "Fondo de pantalla", "Mantén pulsado el escritorio para cambiar el fondo desde Android.");
        addInfoCard(page, "Launcher predeterminado", "Puedes volver al launcher Motorola desde Ajustes > Aplicaciones predeterminadas.");
        TextView system = actionButton("Abrir ajustes de pantalla de inicio", () -> openHomeSettings()); page.addView(system, lp(-1, 50));
        TextView backHome = actionButton("Volver a Motoroll Home", this::showHome); LinearLayout.LayoutParams bh=lp(-1,50); bh.topMargin=dp(10); page.addView(backHome,bh);
    }

    private void addInfoCard(LinearLayout page, String title, String detail) {
        LinearLayout card = column(); card.setPadding(dp(16), dp(14), dp(16), dp(14)); card.setBackground(rounded(PANEL, 18, Color.rgb(32,53,84), 1));
        TextView t = text(title, 14, TEXT, true); TextView d = text(detail, 12, MUTED, false);
        card.addView(t); LinearLayout.LayoutParams dp1=lp(-1,-2); dp1.topMargin=dp(5); card.addView(d,dp1);
        LinearLayout.LayoutParams cp=lp(-1,-2); cp.topMargin=dp(12); page.addView(card,cp);
    }

    private TextView actionButton(String label, Runnable action) {
        TextView b=text(label,13,TEXT,true); b.setGravity(Gravity.CENTER); b.setBackground(gradient(BLUE,PURPLE,16)); b.setOnClickListener(v->action.run()); return b;
    }

    private void launchApp(ResolveInfo info) {
        try {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir la aplicación", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSettings() { safeStart(new Intent(Settings.ACTION_SETTINGS)); }
    private void openHomeSettings() {
        try { safeStart(new Intent(Settings.ACTION_HOME_SETTINGS)); }
        catch (Exception e) { safeStart(new Intent(Settings.ACTION_SETTINGS)); }
    }
    private void openCamera() {
        if (!launchPackage("com.motorola.camera5")) safeStart(new Intent("android.media.action.IMAGE_CAPTURE"));
    }
    private void openGameMode() {
        if (!launchPackage("com.motorola.gamemode")) {
            Toast.makeText(this, "Abre tu juego y utiliza las herramientas GameTime de Motorola.", Toast.LENGTH_LONG).show();
        }
    }
    private void openDialer() { safeStart(new Intent(Intent.ACTION_DIAL)); }
    private void openMessages() {
        Intent i = new Intent(Intent.ACTION_MAIN); i.addCategory(Intent.CATEGORY_APP_MESSAGING); safeStart(i);
    }
    private boolean launchPackage(String pkg) {
        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i == null) return false;
        try { startActivity(i); return true; } catch (Exception e) { return false; }
    }
    private void safeStart(Intent i) {
        try { startActivity(i); } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No hay una aplicación compatible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerOpen) { showHome(); return; }
        super.onBackPressed();
    }

    private static class MotorollMark extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        public MotorollMark(Context c) { super(c); }
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float w=getWidth(), h=getHeight(), cx=w/2f, cy=h/2f, r=Math.min(w,h)*0.44f;
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(Math.max(2f,w*0.045f));
            p.setShader(new LinearGradient(cx-r,cy-r,cx+r,cy+r,CYAN,PURPLE,Shader.TileMode.CLAMP));
            c.drawCircle(cx,cy,r,p); p.setShader(null);
            p.setStyle(Paint.Style.STROKE); p.setStrokeCap(Paint.Cap.ROUND); p.setStrokeJoin(Paint.Join.ROUND); p.setStrokeWidth(Math.max(2.7f,w*0.065f));
            p.setShader(new LinearGradient(cx-r*0.55f,cy,cx+r*0.55f,cy,CYAN,PURPLE,Shader.TileMode.CLAMP));
            Path path=new Path();
            path.moveTo(cx-r*0.52f,cy-r*0.24f);
            path.lineTo(cx-r*0.25f,cy+r*0.24f);
            path.lineTo(cx,cy-r*0.05f);
            path.lineTo(cx+r*0.25f,cy+r*0.24f);
            path.lineTo(cx+r*0.52f,cy-r*0.24f);
            c.drawPath(path,p); p.setShader(null); p.setStyle(Paint.Style.FILL);
        }
    }
}
