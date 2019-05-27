
module Main where
import System.IO


insertOne h i= hPutStrLn h $"insert into play values("++show i++","++show i++")"

deleteOne h i= hPutStrLn h $"delete from play where name="++show i++""

updateOne h i= hPutStrLn h $"update play set name=10 where name="++show i++""

main = do
  h<-openFile "input.sql" WriteMode
  hPutStrLn h "create database db"
  hPutStrLn h "use database db"
  hPutStrLn h "create table play(id int,name int, primary key(id))"
  mapM_ (insertOne h) [1..100]
  mapM_ (deleteOne h) [1,3..15]
  hPutStrLn h "select * from play"
  hPutStrLn h "drop table play"
  hClose h
