/*
 * @(#)StringLayout.java		0.1 16/2/6
 * 
 * Copyright 2016, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.screens.helpers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/*
 * Stores Keys as Constants for use with cgc-i18n
 * 
 * @version 0.1 16/2/6
 * @author Joe Pietruch
 */
public class LanguageKeys 
{
	public static final String main_menu = "main_menu";
	public static final String start_game = "start_game";
	public static final String how_play = "how_play";
	public static final String options = "options";
	public static final String credits = "credits";
	public static final String title = "title";
	public static final String exit = "exit";
	public static final String main_message = "main_message";
	public static final String select = "select";
	public static final String change_item = "change_item";
	public static final String exit_menu = "exit_menu";
	public static final String exit_yes = "exit_yes";
	public static final String exit_no = "exit_no";
	public static final String scroll = "scroll";
	public static final String back = "back";
	public static final String developers = "developers";
	public static final String staff = "staff";
	public static final String backers = "backers";
	public static final String special_thanks = "special_thanks";
	public static final String originator = "originator";
	public static final String lead_web = "lead_web";
	public static final String game_developer = "game_developer";
	public static final String lead_artist = "lead_artist";
	public static final String audio_production = "audio_production";
	public static final String character_artist = "character_artist";
	public static final String ui_designer = "ui_designer";
	public static final String supporting_game_dev = "supporting_game_dev";
	public static final String dark_lord = "dark_lord";
	public static final String operations = "operations";
	public static final String assistant_director = "assistant_director";
	public static final String lab_manager = "lab_manager";
	public static final String associate_director = "associate_director";
	public static final String funded_kickstarter = "funded_kickstarter";
	public static final String hashtag_ftg = "hashtag_ftg";
	public static final String ouya_inc = "ouya_inc";
	public static final String logo_design = "logo_design";
	public static final String kickstarter_planning = "kickstarter_planning";
	public static final String players_deavors = "players_deavors";
	public static final String advice = "advice";
	public static final String family = "family";
	public static final String mud = "mud";
	public static final String will_z = "will_z";
	public static final String chris_r = "chris_r";
	public static final String difficulty_1 = "difficulty_1";
	public static final String difficulty_2 = "difficulty_2";
	public static final String difficulty_3 = "difficulty_3";
	public static final String difficulty_4 = "difficulty_4";
	public static final String difficulty_5 = "difficulty_5";
	public static final String master_volume = "master_volume";
	public static final String music_volume = "music_volume";
	public static final String effects_volume = "effects_volume";
	public static final String stats_tracking = "stats_tracking";
	public static final String sensor_symbols = "sensor_symbols";
	public static final String change_balance = "change_balance";
	public static final String keyboard_layout = "keyboard_layout";
	public static final String parallax_graphics = "parallax_graphics";
	public static final String show_launcher = "show_launcher";
	public static final String accept_changes = "accept_changes";
	public static final String restore_defaults = "restore_defaults";
	public static final String master_volume_message = "master_volume_message";
	public static final String music_volume_message = "music_volume_message";
	public static final String effects_volume_message = "effects_volume_message";
	public static final String stats_message = "stats_message";
	public static final String symbols_message = "symbols_message";
	public static final String balance_message = "balance_message";
	public static final String keyboard_message = "keyboard_message";
	public static final String parallax_message = "parallax_message";
	public static final String launcher_message = "launcher_message";
	public static final String apply_message = "apply_message";
	public static final String revert_message = "revert_message";
	public static final String back_message = "back_message";
	public static final String on = "on";
	public static final String off = "off";
	public static final String no_memory = "no_memory";
	public static final String half_input = "half_input";
	public static final String change_left = "change_left";
	public static final String change_right = "change_right";
	public static final String confirm_right = "confirm_right";
	public static final String previous_right = "previous_right";
	public static final String confirm_left = "confirm_left";
	public static final String previous_left = "previous_left";
	public static final String confirm_choice_left = "confirm_choice_left";
	public static final String confirm_choice_right = "confirm_choice_right";
	public static final String cops_punch_grab = "cops_punch_grab";
	public static final String convicts_punch_grab = "convicts_punch_grab";
	public static final String convicts_mash = "convicts_mash";
	public static final String cops_punch = "cops_punch";
	public static final String tree_punch = "tree_punch";
	public static final String pause = "pause";
	public static final String move_left = "move_left";
	public static final String move_right = "move_right";
	public static final String punch_left = "punch_left";
	public static final String punch_right = "punch_right";
	public static final String jump_left = "jump_left";
	public static final String jump_right = "jump_right";
	public static final String callout_left = "callout_left";
	public static final String callout_right = "callout_right";
	public static final String tutorial = "tutorial";
	public static final String make_moves_son = "make_moves_son";
	public static final String punch = "punch";
	public static final String jump = "jump";
	public static final String who_am_i = "who_am_i";
	public static final String tutorial_move = "tutorial_move";
	public static final String tutorial_chains = "tutorial_chains";
	public static final String tutorial_trees = "tutorial_trees";
	public static final String tutorial_forest = "tutorial_forest";
	public static final String tutorial_mud = "tutorial_mud";
	public static final String tutorial_water = "tutorial_water";
	public static final String tutorial_current = "tutorial_current";
	public static final String tutorial_jump = "tutorial_jump";
	public static final String tutorial_bridge = "tutorial_bridge";
	public static final String tutorial_sensor = "tutorial_sensor";
	public static final String tutorial_icons = "tutorial_icons";
	public static final String tutorial_special = "tutorial_special";
	public static final String tutorial_correct = "tutorial_correct";
	public static final String tutorial_guard = "tutorial_guard";
	public static final String tutorial_train = "tutorial_train";
	public static final String tutorial_escape = "tutorial_escape";
	public static final String join_drop = "join_drop";
	public static final String who_me = "who_me";
	public static final String select_con_cop = "select_con_cop";
	public static final String no_controllers = "no_controllers";
	public static final String setup_controllers = "setup_controllers";
	public static final String no_start = "no_start";
	public static final String setup_start = "setup_start";
	public static final String players_ready = "players_ready";
	public static final String to_continue = "to_continue";
	public static final String now_entering = "now_entering";
	public static final String created_by = "created_by";
	public static final String loving_sheriff = "loving_sheriff";
	public static final String cancel = "cancel";
	public static final String select_map = "select_map";
	public static final String fetch_map = "fetch_map";
	public static final String no_maps = "no_maps";
	public static final String no_maps_choose = "no_maps_choose";
	public static final String primary_sort = "primary_sort";
	public static final String secondary_sort = "secondary_sort";
	public static final String sort_id = "sort_id";
	public static final String sort_name = "sort_name";
	public static final String sort_creator = "sort_creator";
	public static final String sort_size = "sort_size";
	public static final String sort_rating = "sort_rating";
	public static final String sort_favorites = "sort_favorites";
	public static final String sort_own = "sort_own";
	public static final String sort_sync = "sort_sync";
	public static final String sort_none = "sort_none";
	public static final String players_any = "players_any";
	public static final String players_exactly = "players_exactly";
	public static final String players_least = "players_least";
	public static final String players_most = "players_most";
	public static final String players_range = "players_range";
	public static final String get_maps = "get_maps";
	public static final String no_saved_maps = "no_saved_maps";
	public static final String format_changed = "format_changed";
	public static final String unauthorized = "unauthorized";
	public static final String convicts_escaped = "convicts_escaped";
	public static final String favorite_map = "favorite_map";
	public static final String play_again = "play_again";
	public static final String character_select = "character_select";
	public static final String map_select = "map_select";
	public static final String law_prevails = "law_prevails";
	public static final String did_you_like = "did_you_like";
	public static final String map_favorited = "map_favorited";
	public static final String favorite = "favorite";
	public static final String not_enough_maps = "not_enough_maps";
	public static final String already_favorited = "already_favorited";
	public static final String entering_map = "entering_map";
	public static final String by_x = "by_x";
	public static final String pall_top = "pall_top";
	public static final String pall_mid = "pall_mid";
	public static final String pall_bot = "pall_bot";
	public static final String steel_top = "steel_top";
	public static final String steel_mid = "steel_mid";
	public static final String steel_bot = "steel_bot";
	public static final String tank_top = "tank_top";
	public static final String tank_mid = "tank_mid";
	public static final String tank_bot = "tank_bot";
	public static final String train_top = "train_top";
	public static final String train_mid = "train_mid";
	public static final String train_bot = "train_bot";
	public static final String trench_top = "trench_top";
	public static final String trench_mid = "trench_mid";
	public static final String trench_bot = "trench_bot";
	public static final String resume_game = "resume_game";
	public static final String settings = "settings";
	public static final String restart_chase = "restart_chase";
	public static final String select_characters = "select_characters";
	public static final String restart_prompt = "restart_prompt";
	public static final String character_prompt = "character_prompt";
	public static final String map_prompt = "map_prompt";
	public static final String main_prompt = "main_prompt";
	public static final String yes = "yes";
	public static final String no = "no";
}
