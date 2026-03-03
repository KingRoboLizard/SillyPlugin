package dev.celestial.silly.lua;

import dev.celestial.silly.mixin.RuntimeAccessor;
import net.minecraft.nbt.ByteArrayTag;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.lua.FiguraLuaRuntime;
import org.figuramc.figura.lua.LuaNotNil;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.utils.PathUtils;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

@LuaWhitelist
public class BackportsAPI {
    public Avatar owner;
    public FiguraLuaRuntime runtime;
    public BackportsAPI(FiguraLuaRuntime runtime) {
        this.runtime = runtime;
        this.owner = runtime.owner;
    }
    public static Deque<UUID> callerStack = new ArrayDeque<>();

    @LuaWhitelist
    public String getCaller() {
        UUID caller = callerStack.peek();
        if (caller != null) return caller.toString();
        return null;
    }

    @LuaWhitelist
    public void addScript(@LuaNotNil String path, String contents, String side) {
        if (side == null) side = "BOTH";
        side = side.toUpperCase();
        boolean nbt = !side.equals("RUNTIME");
        boolean runtime = !side.equals("NBT");
        RuntimeAccessor runtimeAccessor = ((RuntimeAccessor)this.runtime);
        Map<String, String> scripts = runtimeAccessor.getScripts();
        LuaFunction getInfoFunction = runtimeAccessor.getGetInfoFunction();

        Path filepath = PathUtils.getPath(path);
        Path dir = PathUtils.getWorkingDirectory(getInfoFunction);
        String scriptName = PathUtils.computeSafeString(PathUtils.getPath(PathUtils.computeSafeString(
                PathUtils.isAbsolute(path) ? filepath : dir.resolve(filepath)
        )));
        String scriptNameNbt = scriptName.replace('/','.');

        Map<String, Varargs> loadedScripts = runtimeAccessor.getLoadedScripts();
        if (runtime) loadedScripts.remove(scriptName);
        if (contents == null) {
            if (!nbt) owner.nbt.getCompound("scripts").remove(scriptNameNbt);
            if (!runtime) scripts.remove(scriptName);
            return;
        }
        if (runtime) scripts.put(scriptName, contents);
        if (nbt) owner.nbt.getCompound("scripts").put(scriptNameNbt,new ByteArrayTag(contents.getBytes(StandardCharsets.UTF_8)));
    }

    @LuaWhitelist
    public LuaTable getScripts(String path) {
        // iterate over all script names and add them if their name starts with the path query
        Map<String, String> scripts = ((RuntimeAccessor)runtime).getScripts();
        LuaTable table = new LuaTable();
        if(path.isEmpty()){
            for (String s : scripts.keySet()) {
                table.set(s,scripts.get(s));
            }
        }else{
            for (String s : scripts.keySet()) {
                if(!s.startsWith(path)) continue;
                table.set(s,scripts.get(s));
            }
        }

        return table;
    }

    @LuaWhitelist
    public String getScript(String scriptPath) {
        RuntimeAccessor runtimeAccessor = ((RuntimeAccessor)this.runtime);
        Map<String, String> scripts = runtimeAccessor.getScripts();
        LuaFunction getInfoFunction = runtimeAccessor.getGetInfoFunction();
        Path path = PathUtils.getPath(scriptPath);
        return scripts.get(PathUtils.computeSafeString(PathUtils.isAbsolute(path) ? path : PathUtils.getWorkingDirectory(getInfoFunction).resolve(path)));
    }
}
