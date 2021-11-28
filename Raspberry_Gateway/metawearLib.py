from pymetawear.client import MetaWearClient
from mbientlab.metawear import MetaWear, libmetawear
from mbientlab.metawear.cbindings import *
from threading import Event
from time import sleep

def getInfo(mac):

    device = MetaWearClient(mac)
    
    # Switch on led
    pattern = LedPattern(repeat_count= Const.LED_REPEAT_INDEFINITELY)
    libmetawear.mbl_mw_led_load_preset_pattern(byref(pattern), LedPreset.BLINK)
    libmetawear.mbl_mw_led_write_pattern(device.board, byref(pattern), LedColor.RED)
    libmetawear.mbl_mw_led_play(device.board)
    
    
    sleep(30)
    
    # Switch off led
    libmetawear.mbl_mw_led_stop_and_clear(device.board)

    device.disconnect()
    
    
    
    
