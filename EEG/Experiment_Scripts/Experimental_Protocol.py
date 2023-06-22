import numpy as np
import pygame
from threading import Timer
from matplotlib.pyplot import draw
from collections import defaultdict as dd
import random
import socket

### Code for experimental protocol.
#  60 trials (42 normal, 9 False start, 9 False continue)
# Different trial cases: Normal, Error_start, Error_continue
# EEG data being recorded: 
# Left-hand MI, 
# Right-hand MI,
# Error-related potential (ERRP) if WC continues moving even if signaled to stop,
# ERRP if WC starts moving without being signalled to GO (intent)


# CONSTANTS
RED = (254,0,0)
GREEN = (0,250,15)
PEACEFUL_BLUE = (30,130,240)
WHITE = (0,0,0)
BLACK = (0,0,0)
GREY = (125,125,125)
ORIGIN = (0,0)
# TRIAL TYPES
L_MI_trial = -1
R_MI_trial = 1
REST_trial = 0
TRUE_REST_trial = 2
ERRP_START_trial = 3
ERRP_CONTINUE_trial = 4
# SCENARIOS
NORMAL = (REST_trial, R_MI_trial, REST_trial, L_MI_trial, REST_trial, TRUE_REST_trial)
ERR_START = (ERRP_START_trial, R_MI_trial, REST_trial, L_MI_trial, REST_trial, TRUE_REST_trial)
ERR_CONTINUE  = (REST_trial, R_MI_trial, REST_trial, L_MI_trial, ERRP_CONTINUE_trial, REST_trial, TRUE_REST_trial)
# Note:
# Rest period - REST_trial - is necessary to act as a baseline for EEG classification
# Motor imagery rest is not comparable with ERRP rest

def draw_images(color):
    """
    Display different messages on screen depending on desired participant neural response.
    Left-hand Motor Imagery, Right-hand Motor Imagery, Error related Potential, or Resting baseline.  

    Parameters:
    color -- a tuple with RGB values.

    No output -
    """
     
    color_diode = WHITE

    if color == GREEN:
        msg = 'GO'

    elif color == RED:
        msg = 'STOP'
        color_diode = BLACK

    elif color == PEACEFUL_BLUE:
        msg = 'Rest'
        color_diode = GREY

    else:
        msg = ''

    win.fill(color)
    letter_font = pygame.font.Font('freesansbold.ttf', 250)
    text = letter_font.render(msg, True, WHITE)
    textRect = text.get_rect()
    textRect.center = (win_w//2, win_h//2)
    win.blit(text, textRect)

    # Box diplay for photodiode 
    pygame.draw.rect(win, color_diode, pygame.Rect(30, 30, 60, 60))

    pygame.display.update()


def countdown(surface, sec):
    """
    Display a countdown timer.

    Parameters:
    surface -- A Pygame surface object where the countdown is displayed.
    sec -- The number of seconds for the countdown.
    """

    # position of countdown numbers on the Pygame window
    x = 520
    y = 110
    surface.fill((0, 0, 0))
    myfont = pygame.font.SysFont('Comic Sans MS', 300)

    # display numbers in descending order
    for i in range(sec, 0, -1):
        surface.fill((0, 0, 0))
        text = myfont.render(str(i), False, (160, 160, 160))
        surface.blit(text,(x, y))
        pygame.display.update()
        pygame.time.delay(1000)


def quit_game():
    """
    quit Pygame window
    """
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()


def move_WC_Display_screen(scenario):
    """
    Run the screen display, WC movement, and EEG data classification based on the given scenario.
    Total duration of one trial: 25s regardless of randomisation
    
    Parameters: A tuple containing a series of sequential trial types.

    No output -
    """

### Notes:
# Need to flag and record timestamp of when the error occurs

    quit_game()

    # delta_t will help randomise the timepoint the neural triggers occur so as to avoid the patient becoming accustomed to the timing
    delta_t = random.randint(0,1000)

    ###########      BLank screen (4-5s)      #################
    draw_images(WHITE)

    # Save the data
    bytesToSend = str.encode(str(R_MI_trial))
    UDPServerSocket.sendto(bytesToSend, (localIP, localPort))

    # Error-start
    if scenario[1] == ERR_START:
        # Use rest as buffer for ERRP signal for 1s before and 2s after
        pygame.time.delay(1000)
        pygame.time.delay(3000-delta_t)
        print("Moving! (False start)")  # Start the WC
    
        pygame.time.delay(1000)
    else:
        pygame.time.delay(5000-delta_t)


    ###########     Right MI (5s)   ###########
    draw_images(GREEN)
    pygame.time.delay(5000)


    ###########      Blank screen (4-5s)      #################
    draw_images(WHITE)

    # WC starts moving
    bytesToSend = str.encode(str(L_MI_trial))
    UDPServerSocket.sendto(bytesToSend, (localIP, localPort))
    print("Moving!") # Start the WC

    pygame.time.delay(2000 + delta_t)


    ###########     Left MI (5s) ##########
    draw_images(RED)
    pygame.time.delay(5000)
    print("Stop!") # Stop the WC

    bytesToSend = str.encode(str(L_MI_trial))
    UDPServerSocket.sendto(bytesToSend, (localIP, localPort))

    ###########      BLank screen (4-5s)      #################
    # Check erroneous continue scenario
    draw_images(WHITE)

    bytesToSend = str.encode(str(TRUE_REST_trial))
    UDPServerSocket.sendto(bytesToSend, (localIP, localPort))

    if scenario[4] != ERR_CONTINUE:
        print("Stop") # Stop the WC
    pygame.time.delay(5000 - delta_t)
    

    ###########      True Rest (4-5s)      ##################
    draw_images(PEACEFUL_BLUE)
    # play tiktok for 2-5s
    pygame.time.delay(4000+delta_t)
    
    # Save the data
    bytesToSend = str.encode(str(TRUE_REST_trial))
    UDPServerSocket.sendto(bytesToSend, (localIP, localPort))

    print("One session done!")
    # SEND RECORDED INFO TO COMPUTER/SERVER



def run_():    
    """
    Set number of trials, create scenario sequence for experiment,
    and run visual stimuli + WC movement + EEG recording
    """

    pygame.init()
    n_trials = 60
    n_errp_trials = int(n_trials*30/100)

    # For normal recording
    scenarios = [NORMAL]* (n_trials - n_errp_trials) #+ [ERR_START] * (n_errp_trials//2) + [ERR_CONTINUE] * (n_errp_trials//2)
    random.shuffle(scenarios)
    np.savetxt('scenarios_order.csv', scenarios, delimiter=',')

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

    for scenario in scenarios:
        move_WC_Display_screen(scenario)
        




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



# Note:
# use ifconfig -a to find this IP. If your raspberry pi is the first and only device connected to the ESP32,
# this should be the correct IP by default on the raspberry pi

localIP = "127.0.0.1"
localPort = 12345
bufferSize  = 1024
# Create a datagram socket
UDPServerSocket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
print("UDP server up")

# Create window
win_w, win_h = 1280, 700
# win = pygame.display.set_mode((0, 0), pygame.FULLSCREEN, display=0)
win = pygame.display.set_mode((win_w, win_h))
pygame.display.set_caption('Experiment: Wheelchair movement by MI activation')



if __name__ == "__main__":
    # Start recording stuff
    run_()