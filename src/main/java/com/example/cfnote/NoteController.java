package com.example.cfnote;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@RestController
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping("/notes")
    private List<Note> getAllNote(){
        List<Note> noteList = noteService.getAllNotes();
        Set<String> submissionLinks = new HashSet<String>();
        try {
            URL url = new URL("https://codeforces.com/api/user.status?handle=Modern&from=1&count=50");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            JsonNode result = rootNode.path("result");
            Iterator<JsonNode> submissions = result.elements();
            while (submissions.hasNext()){
                JsonNode submission = submissions.next();
                String link = "https://codeforces.com/contest/" +
                                submission.path("problem").path("contestId") + "/problem/" + submission.path("problem").path("index").asText();
                submissionLinks.add(link);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        List<Note> noteListFinal = new ArrayList<Note>();
        for (Note note : noteList){
            if (submissionLinks.contains(note.getUrl())){
                noteService.deleteNoteById(note.getId());
            }
            else{
                noteListFinal.add(note);
            }
        }
        return noteListFinal;
    }

    @GetMapping("notes/{note_id}")
    private Note getNote(@PathVariable("note_id") long note_id){
        return noteService.getNoteById(note_id);
    }

    @PostMapping("/add")
    private void addNewNote(@RequestBody Note note){
        noteService.addNewNote(note);
    }

    @DeleteMapping("/delete/{note_id}")
    private void deleteNote(@PathVariable("note_id") long note_id){
        noteService.deleteNoteById(note_id);
    }

    @PutMapping("/update/{note_id}")
    private void updateNote(
            @PathVariable("note_id") long note_id,
            @RequestBody Note note
                            ){
        noteService.updateNote(note, note_id);
    }

}
