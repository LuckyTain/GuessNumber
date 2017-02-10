//这个卵东西是我(这个菜逼)瞎写的，欢迎二次开发
package win.luckytain.www;

import java.lang.Integer;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    int number = -1;
    //先初始化数字
    @Override
    public void onEnable() {
        this.getCommand("g").setExecutor(this);
        getLogger().info("猜数字游戏插件开启");
        saveDefaultConfig();
        reloadConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender,Command cmd, String label, String[] args){
        if (sender instanceof ConsoleCommandSender){
            sender.sendMessage("在服务器里输入吧");
            return true;
        }
        if(sender.hasPermission("guessnumber.help") && args[0].equalsIgnoreCase("help")){
            sender.sendMessage(ChatColor.GREEN + "GuessNumber Alpha版本");
            sender.sendMessage(ChatColor.GREEN + "/g start [数字]      例:/g start 233");
            sender.sendMessage(ChatColor.GREEN + "开始一盘0-[数字]的猜数字游戏");
            sender.sendMessage(ChatColor.GREEN + "/g guess [数字]      例:/g guess 666");
            sender.sendMessage(ChatColor.GREEN + "猜猜数字");
            sender.sendMessage(ChatColor.GREEN + "/g reload   重载config");
        }
        // 帮助文档
        if (sender.hasPermission("guessnumber.reload") && args[0].equalsIgnoreCase("reload")){
            reloadConfig();
            sender.sendMessage(getConfig().getString("reload"));
        }
        if(sender.hasPermission("guessnumber.start") && args[0].equalsIgnoreCase("start")){
            if (args.length == 2){
                int shuzi = Integer.parseInt(args[1]);
                String shuzis = String.valueOf(shuzi);
                Random random = new Random();
                number = random.nextInt(shuzi);//生成数字 注意number为答案
                sender.sendMessage(ChatColor.RED + "本次正确答案为" + number + ChatColor.GREEN + "(此消息只有你自己看得到)");
                getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Start").replaceAll("%number%", shuzis)));
            }
            else {
                sender.sendMessage(" 参数错误，请使用/g help查看帮助");
            }
        }
        // 开始游戏 生成数字
        if(sender.hasPermission("guessnumber.guess") && args[0].equalsIgnoreCase("guess")){
            Player p = (Player)sender;
            if(args.length == 2){
                int pn = Integer.parseInt(args[1]);
                if (number == -1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("no start")));
                    return true;
                }
                if(pn < number){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("too small")));
                }
                else if (pn > number){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("too large")));
                }
                else if (pn == number){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("win")));
                    getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Server_win").replaceAll("%player%", sender.getName())));
                    if (getConfig().getBoolean("Reward.enable")){
                        p.giveExp(getConfig().getInt("Reward.exp"));
                        for (String ml:getConfig().getStringList("Console")
                             ) {
                            getServer().dispatchCommand(Bukkit.getConsoleSender(),ml.replaceAll("%player%",p.getDisplayName()));
                        }
                    }
                    // Alpha版本所加入的自动奖励功能
                    number = -1;//猜完了初始化
                }
            }
            else {
                sender.sendMessage("参数错误");
            }
        }
        // 玩家猜数字
        return false;
    }
}

