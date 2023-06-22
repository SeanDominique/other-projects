# Code for experimental protocol.
###  Measure Left-hand MI
###  Right-hand MI
###  ERRP if WC continues moving even if signaled to stop
###  ERRP if WC starts moving without being signalled to GO (intent)



### Mentalab imports ###
import argparse
from ast import arg
from tkinter import END
from unittest import skip
from psychopy import event
import random
from explorepy import Explore
### Experiment's imports ###
import numpy as np
import pygame
from threading import Timer
from matplotlib.pyplot import draw
from collections import defaultdict as dd

# CONSTANTS
RED = (254,0,0)
GREEN = (0,250,15)
PEACEFUL_BLUE = (30,130,240)
WHITE = (255,255,255)
BLACK = (0,0,0)
GREY = (125,125,125)
ORIGIN = (0,0)
WIN_W, WIN_H = 1280, 700
# TRIAL TYPES & MARKERS
L_MI_trial = -1
R_MI_trial = 1
TRUE_REST_trial = 0
BLANK = 5
ERR_START_trial = 2
ERR_CONTINUE_trial = 3
EXPERIMENT_START = 9
EXPERIMENT_END = 50
BLOCK_START = 10
BLOCK_END = 11
# SCENARIOS
NORMAL = (TRUE_REST_trial, BLANK, R_MI_trial, BLANK, L_MI_trial, BLANK)
ERR_START = (TRUE_REST_trial, ERR_START_trial, R_MI_trial, BLANK, L_MI_trial, BLANK)
ERR_CONTINUE  = (TRUE_REST_trial, R_MI_trial, BLANK, L_MI_trial, ERR_CONTINUE_trial, BLANK)
# INSTRUCTIONS
MOVE = 1
STOP = 0


########   "Unimportant functions"   ###########
def draw_images(color):
    # Display message on screen

    win.fill(color)
    
    if color == GREEN:
        msg = 'GO'
        side = int(0.75*WIN_W)
        # Indicator box
        pygame.draw.rect(win, WHITE, pygame.Rect(side, WIN_H/2, 60, 60))

    elif color == RED:
        msg = 'STOP'
        side = int(0.25*WIN_W)
        # Indicator box
        pygame.draw.rect(win, WHITE, pygame.Rect(side, WIN_H/2, 60, 60))

    elif color == PEACEFUL_BLUE:
        msg = 'Rest'

    else:
        msg = ''

    letter_font = pygame.font.Font('freesansbold.ttf', 250)
    text = letter_font.render(msg, True, WHITE)
    textRect = text.get_rect()
    textRect.center = (WIN_W//2, WIN_H//2)
    win.blit(text, textRect)

    pygame.display.update()


def countdown(surface, sec):
    ''' Displays a countdown at start of experiment '''
    x = 520
    y = 110
    surface.fill((0, 0, 0))
    myfont = pygame.font.SysFont('Comic Sans MS', 300)

    for i in range(sec, 0, -1):
        surface.fill((0, 0, 0))
        text = myfont.render(str(i), False, (160, 160, 160))
        surface.blit(text,(x, y))
        pygame.display.update()
        pygame.time.delay(1000)


def quit_game():
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()


########   "Important" functions"   ############
def move_WC_Display_screen(scenario):
### Total duration of one trial: 23s regardless of randomization
# Different trial cases: Normal, Error_start
###

    quit_game()

    delta_t = random.randint(0,2000)

    explore.set_marker(BLOCK_START)  

    ###########      True Rest (2-4s)      ##################
    draw_images(PEACEFUL_BLUE)
    event.clearEvents()
    if scenario[1] == ERR_START_trial:
        print("ERRP")
        explore.set_marker(ERR_START_trial)
    else:
        print("Normal")
        explore.set_marker(TRUE_REST_trial)
    # play tiktok for 2-4s?
    pygame.time.delay(4000-delta_t)
    

    ###########      BLank screen (3-5s)  -->  Possible WC error    #################
    draw_images(BLACK)

    if scenario[1] == ERR_START_trial:
        pygame.time.delay(500)
        bytesToSend = str.encode(str(MOVE))
        print("Moving")
        UDPServerSocket.sendto(bytesToSend, (localIP, localPort))
        pygame.time.delay(1500)
        bytesToSend = str.encode(str(STOP))
        print("Stopped")
        UDPServerSocket.sendto(bytesToSend, (localIP, localPort))
        pygame.time.delay(500+delta_t)
    else:
        pygame.time.delay(3000+delta_t)


    ###########     Right MI (5s)             #################
    draw_images(GREEN)
    event.clearEvents()
    explore.set_marker(R_MI_trial)
    pygame.time.delay(5000)


    ###########      BLank screen (2-4s)      #################
    draw_images(BLACK)
    bytesToSend = str.encode(str(MOVE))
    print("Moving")
    UDPServerSocket.sendto(bytesToSend, (localIP, localPort))
    pygame.time.delay(2000 + delta_t)


    ###########     Left MI (5s)              ###############
    # Note: WC is still moving during this time
    draw_images(RED)
    event.clearEvents()
    explore.set_marker(L_MI_trial)
    pygame.time.delay(5000)


    ###########      BLank screen (2-4s)      #################
    draw_images(BLACK)
    bytesToSend = str.encode(str(STOP))
    print("Stop")
    UDPServerSocket.sendto(bytesToSend, (localIP, localPort))
    pygame.time.delay(4000 - delta_t)
    

    # Mark the end of one 23s block
    explore.set_marker(BLOCK_END)

    print("One block done!")



# mainloop
def run_():    
    # Create and save trial order
    n_blocks = 40
    n_errp_trials = 10
    scenarios = [NORMAL]* (n_blocks - n_errp_trials) + [ERR_START] * n_errp_trials #+ [ERR_CONTINUE] * (n_errp_trials//2)
    random.shuffle(scenarios)
    np.savetxt('scenarios_order.csv', scenarios, delimiter=',')

    # Experiment start instruction
    letter_font = pygame.font.Font('freesansbold.ttf', 250)
    text = letter_font.render("Click to start", True, WHITE)
    textRect = text.get_rect()
    textRect.center = (WIN_W//2, WIN_H//2)
    win.blit(text, textRect)
    pygame.display.update()

    # Click to start
    start = False
    while not start:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
            elif event.type == pygame.MOUSEBUTTONDOWN:
                start = True
                break

    countdown(win, 3)

    # Start experiment (screen display, time stamp labelling, and WC mvt)
    explore.set_marker(EXPERIMENT_START)
    for scenario in scenarios:
        move_WC_Display_screen(scenario)

    # Experiment end
    explore.set_marker(EXPERIMENT_END)
    explore.stop_recording()
    explore.disconnect()




if __name__ == "__main__":

    # Create a datagram socket between WC and computer
    UDPServerSocket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
    print("UDP server up")
    localIP = "192.168.37.104"
    localPort = 4210
    bufferSize = 1024

    # Argparse stuff to make command line inputs simpler
    parser = argparse.ArgumentParser(description="A script to run a visual motor imagery experiment")
    parser.add_argument("-n", "--name", dest="name", type=str, help="Name of the device.")
    parser.add_argument("-f", "--filename", dest="filename", type=str, help="Record file name")
    args = parser.parse_args()

    # Connect to Explore and record data
    explore = Explore()
    explore.connect(device_name=args.name)
    explore.record_data(file_name=args.filename, file_type='csv', do_overwrite=False)
    
    # Pygame setup
    pygame.init()
    win = pygame.display.set_mode((WIN_W, WIN_H))
    pygame.display.set_caption('Experiment: Wheelchair movement by MI activation')

    # Start recording stuff
    run_()





    ###########
    # NOTES:

    # localIP and localPort should be those of the WC's ESP32

    # use ifconfig -a to find this IP. If your pi is the first and only device connected to the ESP32,
    # this should be the orrect IP by default on the raspberry pi

    #######     CONNECT TO EEG via bluetooth      ###########
    # BUFFER_SIZE = 2048
    # LOCAL_UDP_IP = "192.168.4.2"
    # SHARED_UDP_PORT = 4210
    # sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)  # Internet  # UDP
    # sock.bind((LOCAL_UDP_IP, SHARED_UDP_PORT))
    # WINDOW_SIZE_MS = 9000
    # SAMPLE_RATE_HZ = 250
    # N_CH = 8
    # trial_n = 1
    ###########