import time
import pygame 
import numpy as np
import random


def countdown(surface, sec):
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
    # quit game
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()


def display(win, scenario,time_stamps,t0):
    quit_game()
    
    win.fill(BLACK)
    pygame.display.update()
    pygame.time.delay(INTER_STIM_DURATION)
    t0 += INTER_STIM_DURATION
    if (scenario):
        pygame.draw.ellipse(win, RED, pygame.Rect(win_w//2,win_h//2,300,180))
        time_stamps.append((t0))

    else:
        pygame.draw.rect(win, BLUE, pygame.Rect(win_w//2,win_h//2,300,180))
    
    pygame.display.update()
    pygame.time.delay(STIM_DURATION)
    t0+= STIM_DURATION
    return t0
    



def p300_experiment():
    ###### EXPERIMENT SETUP
    pygame.init()

    n_blocks = 100
    n_events = 20

    # For normal recording
    scenarios = [EVENT]* (n_events) + [0] * (n_blocks - n_events) #+ [ERR_CONTINUE] * (n_errp_trials//2)
    random.shuffle(scenarios)
    # np.savetxt('scenarios_order.csv', scenarios, delimiter=',')

    time_stamps = []
    
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

    # start time then run
    t0 = time.process_time()
    time_stamps.append(t0)
    for scenario in scenarios:
        t0 = display(win, scenario, time_stamps,t0)

    np.savetxt("ResearchProject/Timestamps_P300_1.csv", time_stamps, delimiter=',')


# Create window
win_w, win_h = 1280, 700
# win = pygame.display.set_mode((0, 0), pygame.FULLSCREEN, display=0)
win = pygame.display.set_mode((win_w, win_h))
pygame.display.set_caption('Experiment: P300')

#CONSTANTS
EVENT = 1
RED = (254,0,0)
BLUE = (30,130,240)
BLACK = (0,0,0)
STIM_DURATION = 500
INTER_STIM_DURATION = 600


p300_experiment()